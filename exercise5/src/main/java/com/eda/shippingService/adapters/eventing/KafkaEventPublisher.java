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
        //try {
            //TODO Make the publishing to Kafka possible
            // Use objectMapper to create a json string from the message payload
            // Create a new ProducerRecord
            // Add the message id as a header
            // Add the operation as a header
        /* TODO Catch the JsonProcessingException thrown by the objectMapper
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        */
        //TODO Send the record to Kafka with the kafkaTemplate
    }
}
