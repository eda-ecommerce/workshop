package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.events.common.DomainEvent;

import java.util.UUID;

public class ShipmentDelivered extends DomainEvent<ShipmentDTO> {
    public ShipmentDelivered(UUID eventKey, ShipmentDTO payload) {
        super(eventKey, payload);
    }
}