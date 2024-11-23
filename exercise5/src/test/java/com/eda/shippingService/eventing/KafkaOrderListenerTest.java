package com.eda.shippingService.eventing;

import com.eda.shippingService.application.eventHandlers.OrderConfirmedEventHandler;
import com.eda.shippingService.application.eventHandlers.OrderRequestedEventHandler;
import com.eda.shippingService.domain.dto.incoming.OrderRequestedDTO;
import com.eda.shippingService.domain.events.OrderRequested;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import static org.awaitility.Awaitility.waitAtMost;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.eda.shippingService.TestHelpers.quickUUID;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class KafkaOrderListenerTest extends KafkaTest {
    @MockBean
    private OrderConfirmedEventHandler orderConfirmedEventHandler;
    @MockBean
    private OrderRequestedEventHandler orderRequestedEventHandler;

    @Autowired
    private KafkaTemplate<String,String > kafkaTemplate;

    @Test
    void shouldCallOrderConfirmedWithValidEvent() throws IOException {
        Mockito.doNothing().when(orderConfirmedEventHandler).handle(Mockito.any());
        Mockito.doNothing().when(orderRequestedEventHandler).handle(Mockito.any());
        ArgumentCaptor<OrderRequested> requestedCaptor = ArgumentCaptor.forClass(OrderRequested.class);
        String orderRequestedPayload = FileUtils.readFileToString(new File("src/test/java/com/eda/shippingService/eventing/data/orderRequested.json"), StandardCharsets.UTF_8);
        log.info("Payload: {}", orderRequestedPayload);
        var record = new ProducerRecord<String, String>("order", orderRequestedPayload);
        record.headers().add("operation", "requested".getBytes(StandardCharsets.UTF_8));
        record.headers().add("messageId", quickUUID(111).toString().getBytes(StandardCharsets.UTF_8));
        //When
        kafkaTemplate.send(record);
        //Then
        waitAtMost(5, TimeUnit.SECONDS)
                .untilAsserted(
                        () -> Mockito.verify(orderRequestedEventHandler).handle(requestedCaptor.capture()));
        OrderRequested expected = new OrderRequested(null, quickUUID(111), 0,
                new OrderRequestedDTO(
                        quickUUID(123),
                        quickUUID(123456),
                        "20-12-24",
                        "InProcess",
                        List.of(
                                new OrderRequestedDTO.Product(UUID.fromString("a9ae0bea-8b96-4569-82ef-1d7af95fcfab"), 10)
                        )
                ));
        OrderRequested requestedEvent = requestedCaptor.getValue();
        assertEquals(expected.getMessageId(), requestedEvent.getMessageId());
        assertEquals(expected.getMessageValue().products(), requestedEvent.getMessageValue().products());
    }
}
