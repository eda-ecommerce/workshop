package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.incoming.OrderDTO;
import com.eda.shippingService.domain.events.common.DomainEvent;

import java.util.UUID;

public class OrderConfirmed extends DomainEvent<OrderDTO> {
    public OrderConfirmed(UUID eventKey, UUID messageId, long timestamp, OrderDTO payload) {
        super(messageId, eventKey, timestamp, payload);
    }
}
