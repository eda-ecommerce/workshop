package com.eda.shippingService.adapters.eventing;

import com.eda.shippingService.domain.events.common.CustomMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@SuppressWarnings("rawtypes")
public class KafkaEventPublisher implements EventPublisher {
    KafkaTemplate<String, ?> kafkaTemplate;
    ObjectMapper objectMapper;

    @Autowired
    KafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void publish(CustomMessage customMessage, String topic) {
        var springMessage = MessageBuilder
                .withPayload(customMessage.getMessageValue())
                .setHeader("operation", customMessage.getClass().getSimpleName())
                .setHeader("messageId", customMessage.getMessageId())
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, customMessage.getMessageKey())
                .build();
        log.info("Publishing message {} to topic {}", springMessage, topic);
        kafkaTemplate.send(springMessage);
    }
}
