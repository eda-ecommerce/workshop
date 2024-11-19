package com.eda.shippingService.domain.dto.incoming;

import com.eda.shippingService.domain.dto.common.AddressDTO;

import java.util.UUID;

public record SelectShipmentAddressDTO(
        UUID orderId,
        AddressDTO shippingAddress
) {
}
