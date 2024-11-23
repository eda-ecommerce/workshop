package com.eda.shippingService.domain.dto.outgoing;

import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.entity.APackage;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record PackageDTO(
        UUID id,
        UUID trackingNumber,
        @Schema(description = "Dimensions of the package", implementation = PackageDimensions.class)
        PackageDimensions dimensions,
        @Schema(example = "20.0")
        Float weight,
        @Schema(description = "List of products in the package", implementation = OrderLineItemDTO.class)
        List<OrderLineItemDTO> contents
) {
    public record PackageDimensions(@Schema(example = "1.0") Float height,
                                    @Schema(example = "1.0") Float width,
                                    @Schema(example = "1.0") Float depth,
                                    @Schema(example = "3.0") Float volume) {}

    public static PackageDTO fromEntity(APackage aPackage){
        return new PackageDTO(
                aPackage.getId(),
                //Actually sets null if trackingNumber is null
                aPackage.getTrackingNumber() != null ? aPackage.getTrackingNumber() : null,
                new PackageDimensions(
                        aPackage.getDimensions().height(),
                        aPackage.getDimensions().width(),
                        aPackage.getDimensions().depth(),
                        aPackage.getDimensions().volume()
                ),
                aPackage.getWeight(),
                aPackage.getContents().stream()
                        .map(OrderLineItemDTO::fromEntity)
                        .toList()
        );
    }

    public APackage toEntity(){
        return new APackage(
                trackingNumber,
                new com.eda.shippingService.domain.entity.PackageDimensions(
                        dimensions().height(),
                        dimensions().width(),
                        dimensions().depth(),
                        dimensions().volume()),
                weight,
                this.contents().stream()
                        .map(OrderLineItemDTO::toEntity)
                        .toList()
                );
    }
}
