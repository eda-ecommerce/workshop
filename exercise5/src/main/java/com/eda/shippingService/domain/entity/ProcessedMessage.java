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
    private String handlerName;

    public ProcessedMessage(UUID messageId, String handlerName) {
        this.messageId = messageId;
        this.handlerName = handlerName;
    }

}

