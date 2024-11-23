package com.eda.shippingService.adapters.eventing;

import com.eda.shippingService.domain.events.common.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaEventPublisher implements EventPublisher {
    KafkaTemplate<String, String> kafkaTemplate;
    ObjectMapper objectMapper;

    @Autowired
    KafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void publish(Message message, String topic) {
        ProducerRecord<String, String> record = null;
        try {
            record = new ProducerRecord<>(topic, objectMapper
                    .writeValueAsString(message.getMessageValue())
            );
            record.headers().add("messageId", message.getMessageId()
                    .toString()
                    .getBytes());
            //To be removed
            record.headers().add("operation", message.getClass().getSimpleName()
                    .getBytes());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("Publishing record {} to topic {}", record, topic);
        kafkaTemplate.send(record);
    }
}
