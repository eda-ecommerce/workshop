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
    private final KafkaTemplate<String, String> kafkaTemplateString;
    private ObjectMapper objectMapper;

    @Autowired
    KafkaEventPublisher(KafkaTemplate<String, ?> kafkaTemplate, KafkaTemplate<String, String> kafkaTemplateString) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.kafkaTemplateString = kafkaTemplateString;
    }

    @Override
    public void publish(Message message, String topic) {
        //TODO Call the KafkaTemplate either:
        //  1. with a Message (preferred)
        //   - Use the MessageBuilder to create a Message with the payload and custom headers (operation, messageId)
        //   - Set the topic and timestamp in the headers (KafkaHeaders)
        //  2. with a ProducerRecord
        //   - Create a ProducerRecord with the topic and the message
        //    2.1 by using an ObjectMapper to serialize the message
        //     - Use the KafkaTemplate<String, String> to send the serialized message
        //    2.2 by using the messageValue directly
        //     - Use the KafkaTemplate<String, ?> to send the message directly
        // Send message / record with the kafkaTemplate
    }
}
