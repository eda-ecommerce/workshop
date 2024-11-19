package com.eda.shippingService.domain.dto.incoming;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum ExternalShipmentStatus {
    @JsonProperty("shipped")
    SHIPPED,
    @JsonProperty("inDelivery")
    IN_DELIVERY,
    @JsonProperty("delivered")
    DELIVERED,
    @JsonProperty("failed")
    FAILED,
    @JsonProperty("returned")
    RETURNED
}
