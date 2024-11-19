package com.eda.shippingService.domain.dto.incoming;

import java.util.UUID;

public record UpdateShipmentStatusDTO(
        UUID orderId,
        ExternalShipmentStatus externalShipmentStatus
) {
}
