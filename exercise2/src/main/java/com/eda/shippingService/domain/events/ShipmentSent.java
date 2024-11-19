package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.common.AddressDTO;
import com.eda.shippingService.domain.dto.outgoing.PackageDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.eda.shippingService.domain.events.common.DomainEvent;

import java.util.UUID;

public class ShipmentSent extends DomainEvent<ShipmentSent.ShipmentSentPayload> {
    public ShipmentSent(Shipment payload) {
        super(null, new ShipmentSentPayload(
                payload.getOrderId(),
                PackageDTO.fromEntity(payload.getAPackage()),
                AddressDTO.fromEntity(payload.getDestination()),
                payload.getStatus()
        ));
    }

    public record ShipmentSentPayload(
            UUID orderId,
            PackageDTO packageDTO,
            AddressDTO dest,
            ShipmentStatus status
    ){}
}
