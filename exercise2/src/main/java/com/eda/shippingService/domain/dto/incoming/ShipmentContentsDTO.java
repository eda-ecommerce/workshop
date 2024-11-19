package com.eda.shippingService.domain.dto.incoming;

import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;

import java.util.List;
import java.util.UUID;

//Should be like OrderCreatedEvent
public record ShipmentContentsDTO(
        UUID customerId,
        List<OrderLineItemDTO> requestedProducts
) {
    public Shipment toEntity(UUID orderID){
        return new Shipment(
                orderID,
                null,
                null,
                requestedProducts.stream().map(OrderLineItemDTO::toEntity).toList(),
                ShipmentStatus.INCOMPLETE
        );
    }
}
