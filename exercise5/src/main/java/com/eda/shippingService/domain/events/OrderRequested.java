package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.incoming.OrderPayload;
import com.eda.shippingService.domain.events.common.DomainEvent;
import lombok.Getter;

import java.util.UUID;

//Since events are using the payload class, there is direct coupling to a DTO. Bad?
@Getter
public class OrderRequested extends DomainEvent<OrderPayload>{
    public OrderRequested(UUID eventKey, UUID messageId, long timestamp, OrderPayload payload) {
        super(messageId,eventKey, timestamp, payload);
    }
}
