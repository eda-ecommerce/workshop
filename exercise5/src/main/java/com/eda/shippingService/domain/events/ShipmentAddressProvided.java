package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.events.common.DomainEvent;

public class ShipmentAddressProvided extends DomainEvent<ShipmentDTO> {
    public ShipmentAddressProvided(ShipmentDTO shipmentDTO) {
        super(shipmentDTO);
    }
}
