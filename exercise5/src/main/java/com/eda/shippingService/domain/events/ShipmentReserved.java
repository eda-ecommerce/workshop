package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.events.common.DomainEvent;

public class ShipmentReserved extends DomainEvent<ShipmentDTO> {
    public ShipmentReserved(ShipmentDTO payload) {
        super(payload);
    }
}
