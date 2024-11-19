package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.StockDTO;
import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.events.common.DomainEvent;

public class OutOfStock extends DomainEvent<StockDTO> {
    public OutOfStock(Product payload) {
        super(null, new StockDTO(
                payload.getId(),
                payload.getPhysicalStock(),
                payload.getReservedStock(),
                payload.getAvailableStock()
        ));
    }
}
