package com.eda.shippingService.domain.events.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

import java.util.UUID;

@Getter
@JsonSerialize
@JsonIgnoreProperties
public abstract class Message<T> {
    private final UUID messageId;
    private final UUID messageKey;
    private final long timestamp;
    private final T messageValue;

    public Message(T messageValue) {
        this(null, messageValue);
    }

    public Message(UUID messageKey, T messageValue) {
        this.messageKey = messageKey;
        this.timestamp = System.currentTimeMillis();
        this.messageId = UUID.randomUUID();
        this.messageValue = messageValue;
    }

    public Message(UUID messageId, UUID messageKey, long timestamp, T messageValue) {
        this.messageId = messageId;
        this.messageKey = messageKey;
        this.timestamp = timestamp;
        this.messageValue = messageValue;
    }
}

