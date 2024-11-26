package com.eda.shippingService.domain.entity;

import com.eda.shippingService.adapters.repo.MessageCompositeKey;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@IdClass(MessageCompositeKey.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProcessedMessage {
    @Id
    private UUID messageId;
    @Id
    private String listenerName;

    public ProcessedMessage(UUID messageId, String listenerName) {
        this.messageId = messageId;
        this.listenerName = listenerName;
    }

}

