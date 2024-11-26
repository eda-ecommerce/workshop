package com.eda.shippingService.exercise5;

import com.eda.shippingService.application.service.IdempotentcyService;
import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.dto.incoming.ShipmentContentsDTO;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.eda.shippingService.helper.KafkaTest;
import com.eda.shippingService.helper.TestHelpers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.eda.shippingService.helper.TestHelpers.quickUUID;
import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public class E5KafkaOrderListenerTest extends KafkaTest {
    private final String orderRequestedString = FileUtils.readFileToString(new File("src/test/java/com/eda/shippingService/eventing/data/given/orderRequested.json"), StandardCharsets.UTF_8);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @MockBean
    private ShipmentService shipmentService;
    @MockBean
    private IdempotentcyService idempotentcyService;

    public E5KafkaOrderListenerTest() throws IOException {
    }

    @Override
    @BeforeEach
    public void setUpEach() throws InterruptedException {
        super.setUpEach();
        Mockito.when(idempotentcyService.hasBeenProcessed(any(UUID.class), any(String.class))).thenReturn(false);
        Mockito.when(idempotentcyService.hasBeenProcessed(any())).thenReturn(false);
        Mockito.doNothing().when(idempotentcyService).saveProcessedMessage(any());
    }

    @Test
    void shouldCallShipmentServiceWithCorrectDTO() throws IOException, InterruptedException {
        Thread.sleep(5000);
        var orderRequested = new ProducerRecord<String, String>("order", orderRequestedString);
        orderRequested.headers().add("operation", "requested".getBytes());
        orderRequested.headers().add("messageId", UUID.randomUUID().toString().getBytes());
        kafkaTemplate.send(orderRequested).join();
        var dtoBuilder = new TestHelpers.ShipmentDTOBuilder();
        var givenShipmentDTO = dtoBuilder
                .withOrderId(quickUUID(123))
                .withStatus(ShipmentStatus.INCOMPLETE)
                .withRequestedProduct(quickUUID(1234567), 10)
                .build();
        Mockito.when(shipmentService.provideRequestedContents(any(UUID.class), any(ShipmentContentsDTO.class)))
                .thenReturn(givenShipmentDTO);
        ArgumentCaptor<UUID> uuidArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<ShipmentContentsDTO> shipmentContentsDTOArgumentCaptor = ArgumentCaptor.forClass(ShipmentContentsDTO.class);
        waitAtMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> Mockito.verify(shipmentService).provideRequestedContents(uuidArgumentCaptor.capture(), shipmentContentsDTOArgumentCaptor.capture()));
        var capturedDTO = shipmentContentsDTOArgumentCaptor.getValue();
        var expectedShipmentContentsDTO = new ShipmentContentsDTO(
                quickUUID(123456),
                List.of(new OrderLineItemDTO(quickUUID(1234567), 10))
        );
        assertEquals(expectedShipmentContentsDTO, capturedDTO);
        assertEquals(quickUUID(123), uuidArgumentCaptor.getValue());
    }

    @Test
    void bonusShouldCallIdempotencyService() throws InterruptedException {
        Mockito.when(idempotentcyService.hasBeenProcessed(any(UUID.class), any(String.class))).thenReturn(true);
        Mockito.when(idempotentcyService.hasBeenProcessed(any())).thenReturn(true);
        Thread.sleep(5000);
        var orderRequested = new ProducerRecord<String, String>("order", orderRequestedString);
        orderRequested.headers().add("operation", "requested".getBytes());
        orderRequested.headers().add("messageId", UUID.randomUUID().toString().getBytes());
        kafkaTemplate.send(orderRequested).join();
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        waitAtMost(10, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> Mockito.verify(idempotentcyService).hasBeenProcessed(captor.capture(), any(String.class))
                );
        Mockito.verify(shipmentService, never()).provideRequestedContents(any(), any());
    }
}
