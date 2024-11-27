package com.eda.shippingService.adapters.eventing;

import com.eda.shippingService.domain.events.common.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@SuppressWarnings({"rawtypes", "LoggingSimilarMessage"})
public class KafkaEventPublisher implements EventPublisher {
    private final KafkaTemplate<String, ?> kafkaTemplate;
    @Autowired
    KafkaEventPublisher(KafkaTemplate<String, ?> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(Message message, String topic) {
        var msg = MessageBuilder.withPayload(message.getMessageValue())
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, message.getMessageKey())
                .setHeader("operation", message.getClass().getSimpleName())
                .setHeader("messageId", message.getMessageId())
                .build();
        kafkaTemplate.send(msg);
    }
}
