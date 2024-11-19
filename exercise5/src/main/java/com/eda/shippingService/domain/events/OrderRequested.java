package com.eda.shippingService.domain.events;

import com.eda.shippingService.domain.dto.incoming.OrderRequestedDTO;
import com.eda.shippingService.domain.events.common.DomainEvent;
import lombok.Getter;

import java.util.UUID;

//Since events are using the payload class, there is direct coupling to a DTO. Bad?
@Getter
public class OrderRequested extends DomainEvent<OrderRequestedDTO>{
    public OrderRequested(UUID eventKey, UUID messageId, long timestamp, OrderRequestedDTO payload) {
        super(messageId,eventKey, timestamp, payload);
    }
}
