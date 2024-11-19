package com.eda.shippingService.adapters.repo;

import com.eda.shippingService.domain.entity.Shipment;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ShipmentRepository extends CrudRepository<Shipment, UUID> {
    Shipment findByOrderId(UUID orderId);
}
