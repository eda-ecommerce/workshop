package com.eda.shippingService.domain.dto.common;

import com.eda.shippingService.domain.entity.OrderLineItem;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record OrderLineItemDTO(
        @JsonProperty("productId")
        UUID productId,
        @JsonProperty("quantity")
        int quantity
) {
    public OrderLineItem toEntity(){
        return new OrderLineItem(
                this.productId(),
                this.quantity()
        );
    }
    public static OrderLineItemDTO fromEntity(OrderLineItem orderLineItem){
        return new OrderLineItemDTO(
                orderLineItem.productId(),
                orderLineItem.quantity()
        );
    }
}
