package com.eda.shippingService.domain.commands;

import com.eda.shippingService.domain.dto.incoming.BoxShipmentDTO;
import com.eda.shippingService.domain.events.common.Command;

import java.util.UUID;

public class BoxShipment extends Command<BoxShipmentDTO> {
    //Can be created via Controller, will be marked with current timestamp and random UUID
    public BoxShipment(BoxShipmentDTO payload) {
        super(payload);
    }
    public BoxShipment(UUID messageId, long timestamp, BoxShipmentDTO payload) {
        super(messageId, null, timestamp, payload );
    }
}
