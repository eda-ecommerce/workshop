package com.eda.shippingService.domain.dto.incoming;

import com.eda.shippingService.domain.dto.common.PackageDimensionsDTO;
import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.entity.APackage;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record IncomingPackageDTO(
        PackageDimensionsDTO dimensions,
        Float weight,
        List<OrderLineItemDTO> contents
) {
    public APackage toPackage() {
        return new APackage(
                dimensions().height(),
                dimensions().width(),
                dimensions().depth(),
                weight,
                this.contents().stream()
                        .map(OrderLineItemDTO::toEntity)
                        .toList()
        );
    }
}
