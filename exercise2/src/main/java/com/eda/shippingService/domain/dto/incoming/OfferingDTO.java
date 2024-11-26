package com.eda.shippingService.domain.dto.incoming;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record OfferingDTO(
        @JsonProperty("id") UUID id,
        @JsonProperty("productId") UUID productId,
        @JsonProperty("quantity") int quantity,
        @JsonProperty("price") double price,
        @JsonProperty("status") String status
) {}