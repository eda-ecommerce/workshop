package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.incoming.OrderConfirmedDTO;
import com.eda.shippingService.domain.events.common.DomainEvent;

import java.util.UUID;

public class OrderConfirmed extends DomainEvent<OrderConfirmedDTO> {
    public OrderConfirmed(UUID eventKey, UUID messageId, long timestamp, OrderConfirmedDTO payload) {
        super(eventKey, messageId, timestamp, payload);
    }
}
