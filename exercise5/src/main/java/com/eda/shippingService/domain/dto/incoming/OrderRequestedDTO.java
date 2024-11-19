package com.eda.shippingService.domain.dto.incoming;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderRequestedDTO(
        @JsonProperty("orderId") UUID orderId,
        @JsonProperty("customerId") UUID customerId,
        @JsonProperty("orderDate") String orderDate,
        @JsonProperty("orderStatus") String orderStatus,
        @JsonProperty("products") List<Product> products
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Product(
            @JsonProperty("productId") UUID productId,
            @JsonProperty("quantity") int quantity
    ) {}
}