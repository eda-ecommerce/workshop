package com.eda.shippingService.domain.events.common;

import java.util.UUID;

public abstract class DomainEvent<T> extends Message<T>{
    public DomainEvent(T payload) {
        super(payload);
    }
    public DomainEvent(UUID eventKey, T payload) {
        super(eventKey, payload);
    }
    public DomainEvent(UUID messageId, UUID eventKey,  long timestamp, T payload) {
        super(messageId,eventKey, timestamp, payload);
    }
}
