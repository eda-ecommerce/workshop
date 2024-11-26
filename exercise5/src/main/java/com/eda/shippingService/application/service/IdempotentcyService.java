package com.eda.shippingService.application.service;

import com.eda.shippingService.adapters.repo.IdempotentHandlerRepository;
import com.eda.shippingService.domain.entity.ProcessedMessage;
import com.eda.shippingService.domain.events.common.Command;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@SuppressWarnings("rawtypes")
@Service
public class IdempotentcyService {
    private final IdempotentHandlerRepository idempotentcyRepository;

    @Autowired
    public IdempotentcyService(IdempotentHandlerRepository idempotentcyRepository) {
        this.idempotentcyRepository = idempotentcyRepository;
    }

    public boolean hasBeenProcessed(ConsumerRecord<String, String> record, String handlerName) {
        return idempotentcyRepository.findByMessageIdAndListenerName(UUID.fromString(new String(record.headers().lastHeader("messageId").value())), handlerName).isPresent();
    }

    /**
     */
    public boolean hasBeenProcessed(UUID messageId, String handlerName) {
        return idempotentcyRepository.findByMessageIdAndListenerName(messageId, handlerName).isPresent();
    }

    public boolean hasBeenProcessed(Command command) {
        return idempotentcyRepository.findByMessageIdAndListenerName(command.getMessageId(), command.getClass().getSimpleName()).isPresent();
    }

    public void saveProcessedMessage(UUID messageId, String handlerName) {
        idempotentcyRepository.save(new ProcessedMessage(messageId, handlerName));
    }

    public void saveProcessedMessage(Command command) {
        idempotentcyRepository.save(new ProcessedMessage(command.getMessageId(), command.getClass().getSimpleName()));
    }

    public void saveProcessedMessage(ConsumerRecord<String, String> record, String listenerName) {
        idempotentcyRepository.save(new ProcessedMessage(UUID.fromString(new String(record.headers().lastHeader("messageId").value())), listenerName));
    }
}
