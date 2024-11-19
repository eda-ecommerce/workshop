package com.eda.shippingService.domain.events.common;

import java.util.UUID;

public abstract class Command<T> extends Message<T> {
    public Command(T payload) {
        super(payload);
    }
    public Command(UUID eventKey, T payload) {
        super(eventKey, payload);
    }
    public Command(UUID messageId, UUID eventKey,  long timestamp, T payload) {
        super(messageId,eventKey, timestamp, payload);
    }
}
