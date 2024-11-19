package com.eda.shippingService.application.queryHandlers;

import com.eda.shippingService.adapters.repo.ShipmentRepository;

public class ListAllShipments {

    private ShipmentRepository shipmentRepository;

    public Object listAllShipments() {
        return shipmentRepository.findAll();
    }
}