package com.eda.shippingService.domain.dto.incoming;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record OrderPayload(
        @JsonProperty("id") UUID orderId,
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