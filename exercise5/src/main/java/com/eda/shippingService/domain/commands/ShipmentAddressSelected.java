package com.eda.shippingService.domain.commands;

import com.eda.shippingService.domain.dto.incoming.SelectShipmentAddressDTO;
import com.eda.shippingService.domain.events.common.Command;

import java.util.UUID;

public class ShipmentAddressSelected extends Command<SelectShipmentAddressDTO> {
    public ShipmentAddressSelected(UUID messageId, long timestamp, SelectShipmentAddressDTO payload) {
        super(messageId, null, timestamp, payload);
    }
}
