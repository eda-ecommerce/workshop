package com.eda.shippingService.adapters.eventing;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@NoArgsConstructor
public class KafkaOrderListener {
    //This should probably more fine-grained
    @KafkaListener(topics = "${kafka.topic.order}")
    public void listen(ConsumerRecord<String, String> record) {
        //TODO: Consume
        log.info("Received message with key: {} and value: {}", record.key(), record.value());

    }
}