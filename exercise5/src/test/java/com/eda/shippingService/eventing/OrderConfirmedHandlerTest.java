package com.eda.shippingService.eventing;

import com.eda.shippingService.application.eventHandlers.OrderConfirmedEventHandler;
import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.domain.dto.incoming.OrderConfirmedDTO;
import com.eda.shippingService.domain.entity.OrderLineItem;
import com.eda.shippingService.domain.entity.ProcessedMessage;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.eda.shippingService.domain.events.OrderConfirmed;
import com.eda.shippingService.adapters.repo.IdempotentHandlerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.eda.shippingService.TestHelpers.quickAddress;
import static com.eda.shippingService.TestHelpers.quickUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {OrderConfirmedEventHandler.class, ShipmentService.class})
public class OrderConfirmedHandlerTest {
    @Autowired
    private OrderConfirmedEventHandler orderConfirmedEventHandler;

    @MockBean
    private ShipmentService shipmentService;
    @MockBean
    IdempotentHandlerRepository idempotentHandlerRepository;

    @Test
    public void shouldApproveShipment() {
        //Given
        Shipment shipment = new Shipment(
                quickUUID(1),
                quickAddress("Street1"),
                null,
                List.of(
                        new OrderLineItem(quickUUID(2), 1)
                ),
                ShipmentStatus.INCOMPLETE
        );
        OrderConfirmed orderConfirmedEvent = new OrderConfirmed(null, quickUUID(3), System.currentTimeMillis(),
                new OrderConfirmedDTO(quickUUID(1), quickUUID(99), "23-12-2021", "READY_FOR_SHIPMENT", List.of(
                        new OrderConfirmedDTO.Product(quickUUID(2), 1)
                )
                ));
        //Mocks
        Mockito.when(idempotentHandlerRepository.findByMessageIdAndHandlerName(Mockito.any(), Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(idempotentHandlerRepository.save(Mockito.any())).thenReturn(Mockito.mock(ProcessedMessage.class));
        Mockito.doNothing().when(shipmentService).approveShipment(Mockito.any());
        //When
        orderConfirmedEventHandler.handle(orderConfirmedEvent);

        //Then
        Mockito.verify(shipmentService, Mockito.times(1)).approveShipment(quickUUID(1));
        ArgumentCaptor<UUID> shipmentCaptor = ArgumentCaptor.forClass(UUID.class);
        Mockito.verify(shipmentService).approveShipment(shipmentCaptor.capture());
        UUID savedShipment = shipmentCaptor.getValue();
        assertEquals(shipment.getOrderId(), savedShipment);
    }
}
