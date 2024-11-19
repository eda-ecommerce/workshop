package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.common.AddressDTO;
import com.eda.shippingService.domain.dto.outgoing.PackageDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.eda.shippingService.domain.events.common.DomainEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.UUID;

public class ShipmentBoxed extends DomainEvent<ShipmentBoxed.ShipmentBoxedPayload> {
    public ShipmentBoxed(Shipment payload) {
        super(new ShipmentBoxedPayload(
                payload.getOrderId(),
                PackageDTO.fromEntity(payload.getAPackage()),
                AddressDTO.fromEntity(payload.getDestination()),
                payload.getStatus()
        ));
    }
    @JsonSerialize
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ShipmentBoxedPayload(
            @JsonProperty("orderId")
            UUID orderId,
            @JsonProperty("package")
            PackageDTO packageDTO,
            @JsonProperty("destination")
            AddressDTO dest,
            @JsonProperty("status")
            ShipmentStatus status
    ){}
}
