package com.eda.shippingService.domain.dto.incoming;

import com.eda.shippingService.domain.dto.common.AddressDTO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.UUID;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record SelectShipmentAddressDTO(
        UUID orderId,
        AddressDTO shippingAddress
) {
}
