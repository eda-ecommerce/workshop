package com.eda.shippingService.application.queryHandlers;

import com.eda.shippingService.adapters.repo.ShipmentRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component

public class GetShipmentByID {

    private ShipmentRepository shipmentRepository;

    public Object getShipmentByID(UUID shipmentId) {

        // using if coz elvis operator doesn't work for some reason
        if (shipmentRepository.findById(shipmentId).isEmpty()) {
            throw new IllegalArgumentException(String.format("Shipment with shipment ID %s does not exist.", shipmentId));
        }

        return shipmentRepository.findById(shipmentId);
    }
}
