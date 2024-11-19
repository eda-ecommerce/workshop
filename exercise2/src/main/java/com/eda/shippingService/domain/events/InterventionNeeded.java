package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.events.common.DomainEvent;

public class InterventionNeeded extends DomainEvent<ShipmentDTO> {
    public InterventionNeeded(ShipmentDTO payload) {
        super(null, payload);
    }
}
