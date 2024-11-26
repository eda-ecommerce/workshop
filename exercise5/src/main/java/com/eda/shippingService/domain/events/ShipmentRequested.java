package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.events.common.DomainEvent;
import com.eda.shippingService.domain.events.common.Message;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ShipmentRequested extends DomainEvent<ShipmentDTO> {
    public ShipmentRequested(ShipmentDTO shipmentDTO) {
        super(shipmentDTO);
    }
}
