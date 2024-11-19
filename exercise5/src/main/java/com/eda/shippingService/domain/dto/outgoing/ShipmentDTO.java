package com.eda.shippingService.domain.dto.outgoing;
import com.eda.shippingService.domain.dto.common.AddressDTO;
import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record ShipmentDTO(
        @JsonProperty("orderId")
        UUID orderId,
        @JsonProperty("destination")
        AddressDTO destination,
        @JsonProperty("package")
        PackageDTO aPackage,
        @JsonProperty("requestedProducts")
        List<OrderLineItemDTO> requestedProducts,
        @JsonProperty("status")
        ShipmentStatus status
) {
    public Shipment toEntity(){
        return new Shipment(orderId,
                destination.toEntity(),
                aPackage != null ? aPackage.toEntity() : null,
                this.requestedProducts().stream()
                        .map(OrderLineItemDTO::toEntity)
                        .toList(),
                status
        );
   }

   public static ShipmentDTO fromEntity(Shipment shipment){
        return new ShipmentDTO(
                shipment.getOrderId(),
                AddressDTO.fromEntity(shipment.getDestination()),
                shipment.getAPackage() != null ?PackageDTO.fromEntity(shipment.getAPackage()) : null,
                shipment.getRequestedProducts().stream()
                        .map(OrderLineItemDTO::fromEntity).toList(),
                shipment.getStatus()
        );
   }
}