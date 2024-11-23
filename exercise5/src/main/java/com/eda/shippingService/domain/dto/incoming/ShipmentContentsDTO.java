package com.eda.shippingService.domain.dto.incoming;

import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

//Should be like OrderCreatedEvent
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record ShipmentContentsDTO(
        UUID customerId,
        @Schema(description = "List of requested products in the shipment", implementation = OrderLineItemDTO.class)
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
