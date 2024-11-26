package com.eda.shippingService.exercise5;

import com.eda.shippingService.adapters.eventing.KafkaEventPublisher;
import com.eda.shippingService.adapters.repo.ShipmentRepository;
import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.application.service.StockServiceImpl;
import com.eda.shippingService.application.service.exception.NotEnoughStockException;
import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.dto.incoming.ShipmentContentsDTO;
import com.eda.shippingService.domain.entity.OrderLineItem;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.eda.shippingService.domain.events.ShipmentReserved;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static com.eda.shippingService.helper.TestHelpers.quickUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = {ShipmentService.class})
public class E5ShipmentServiceTest {
    @MockBean
    private KafkaEventPublisher eventPublisher;
    @MockBean
    private ShipmentRepository shipmentRepository;
    @MockBean
    private StockServiceImpl stockService;

    @Value("${kafka.topic.shipment}")
    private String shipmentTopic;

    @Autowired
    private ShipmentService shipmentService;

    @Test
    void shouldCallEventPublisherWithCorrectEvent() throws NotEnoughStockException {
        Mockito.doNothing().when(eventPublisher).publish(any(), any());
        Mockito.doNothing().when(stockService).reserveStock(any(), any(Integer.class));
        var givenShipmentDto = new ShipmentContentsDTO(
                quickUUID(123),
                List.of(new OrderLineItemDTO(quickUUID(111), 10))
        );
        Mockito.when(shipmentRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(shipmentRepository.save(any(Shipment.class))).thenReturn(
                new Shipment(quickUUID(123),
                        null,
                        null,
                        List.of(new OrderLineItem(quickUUID(111), 10)),
                        ShipmentStatus.INCOMPLETE));

        //when
        shipmentService.provideRequestedContents(quickUUID(123),
                givenShipmentDto
        );
        //then
        Mockito.verify(eventPublisher).publish(any(ShipmentReserved.class), eq(shipmentTopic));
    }

}
