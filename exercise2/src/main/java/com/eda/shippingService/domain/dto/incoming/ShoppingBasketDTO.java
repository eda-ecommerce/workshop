package com.eda.shippingService.domain.dto.incoming;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record ShoppingBasketDTO(
        @JsonProperty("shoppingBasketId") UUID shoppingBasketId,
        @JsonProperty("customerId") UUID customerId,
        @JsonProperty("totalPrice") double totalPrice,
        @JsonProperty("totalItemQuantity") int totalItemQuantity,
        @JsonProperty("items") List<ShoppingBasketItemDTO> items
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record ShoppingBasketItemDTO(
            @JsonProperty("shoppingBasketItemId") UUID shoppingBasketItemId,
            @JsonProperty("shoppingBasketId") UUID shoppingBasketId,
            @JsonProperty("offeringId") UUID offeringId,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("totalPrice") double totalPrice,
            @JsonProperty("itemState") String itemState
    ) {}
}
