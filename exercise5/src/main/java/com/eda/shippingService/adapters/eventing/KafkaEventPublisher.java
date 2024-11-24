package com.eda.shippingService.adapters.eventing;

import com.eda.shippingService.domain.events.common.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@SuppressWarnings({"rawtypes", "LoggingSimilarMessage"})
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
        ProducerRecord<String, String> record;
        try {
            //TODO Use objectMapper to create a json string from the payload
            //TODO Create new ProducerRecord
            //TODO Add the message id as a header
            //TODO Add the operation as a header
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("Publishing record {} to topic {}", record, topic);
        //TODO Send the record with the kafkaTemplate
    }
}
