package com.eda.shippingService.domain.dto.outgoing;

import com.eda.shippingService.domain.entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.UUID;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public record StockDTO(
        UUID productId,
        Integer actualStock,
        Integer reservedStock,
        Integer availableStock
) {
    public static StockDTO fromProduct(Product product){
        return new StockDTO(
                product.getId(),
                product.getPhysicalStock(),
                product.getReservedStock(),
                product.getAvailableStock()
        );
    }
}
