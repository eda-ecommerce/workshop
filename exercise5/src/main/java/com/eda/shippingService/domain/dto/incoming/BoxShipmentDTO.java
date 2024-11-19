package com.eda.shippingService.domain.dto.incoming;

import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.entity.APackage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.UUID;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public record BoxShipmentDTO(
        UUID orderId,
        List<OrderLineItemDTO> contents,
        Float height,
        Float width,
        Float depth,
        Float weight
) {
    public APackage toPackage() {
        return new APackage(
             height,width,depth,weight,
                contents.stream().map(OrderLineItemDTO::toEntity).toList()
        );
    }
}
