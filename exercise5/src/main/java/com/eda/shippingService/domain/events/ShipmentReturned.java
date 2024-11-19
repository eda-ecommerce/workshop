package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.events.common.DomainEvent;

public class ShipmentReturned extends DomainEvent<ShipmentDTO> {
    public ShipmentReturned(ShipmentDTO payload) {
        super(null, payload);
    }
}
