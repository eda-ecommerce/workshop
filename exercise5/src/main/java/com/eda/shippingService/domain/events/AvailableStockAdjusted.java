package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.outgoing.StockDTO;
import com.eda.shippingService.domain.events.common.DomainEvent;

public class AvailableStockAdjusted extends DomainEvent<StockDTO> {
    public AvailableStockAdjusted(StockDTO payload) {
        super(payload);
    }
}
