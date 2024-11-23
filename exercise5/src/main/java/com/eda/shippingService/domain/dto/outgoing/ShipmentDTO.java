package com.eda.shippingService.domain.dto.outgoing;
import com.eda.shippingService.domain.dto.common.AddressDTO;
import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(description = "Data Transfer Object for Shipment")
public record ShipmentDTO(
        @JsonProperty("orderId")
        @Schema(description = "Unique identifier for the order", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID orderId,

        @JsonProperty("destination")
        @Schema(description = "Destination address for the shipment", implementation = AddressDTO.class)
        AddressDTO destination,

        @JsonProperty("package")
        @Schema(description = "Package details for the shipment", implementation = PackageDTO.class)
        PackageDTO aPackage,

        @JsonProperty("requestedProducts")
        @Schema(description = "List of requested products in the shipment", implementation = OrderLineItemDTO.class)
        List<OrderLineItemDTO> requestedProducts,

        @JsonProperty("status")
        @Schema(description = "Current status of the shipment", implementation =  ShipmentStatus.class)
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
                shipment.getDestination() != null ? AddressDTO.fromEntity(shipment.getDestination()): null,
                shipment.getAPackage() != null ?PackageDTO.fromEntity(shipment.getAPackage()) : null,
                shipment.getRequestedProducts().stream()
                        .map(OrderLineItemDTO::fromEntity).toList(),
                shipment.getStatus()
        );
    }
}