package com.eda.shippingService.adapters.eventing;

import com.eda.shippingService.application.eventHandlers.EventHandler;
import com.eda.shippingService.domain.dto.incoming.OrderConfirmedDTO;
import com.eda.shippingService.domain.dto.incoming.OrderRequestedDTO;
import com.eda.shippingService.domain.events.OrderConfirmed;
import com.eda.shippingService.domain.events.OrderRequested;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class KafkaOrderListener {

    @Setter
    private EventHandler<OrderRequested> orderRequestedEventHandler;
    @Setter
    private EventHandler<OrderConfirmed> orderConfimedEventEventHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public KafkaOrderListener(EventHandler<OrderRequested> orderRequestedEventHandler, EventHandler<OrderConfirmed> orderConfimedEventEventHandler) {
        this.orderRequestedEventHandler = orderRequestedEventHandler;
        this.orderConfimedEventEventHandler = orderConfimedEventEventHandler;
    }

    //This should probably more fine-grained
    @KafkaListener(topics = "${kafka.topic.order}")
    public void listen(ConsumerRecord<String, String> record) {
        var headers = record.headers().toArray();
        var operation = Arrays.stream(headers)
                .filter(header -> header.key().equals("operation"))
                .findFirst()
                .map(header -> new String(header.value()))
                .orElseThrow();
        var messageId = UUID.fromString(Arrays.stream(headers)
                .filter(header -> header.key().equals("messageId"))
                .findFirst()
                .map(header -> new String(header.value()))
                .orElseThrow());
        log.info("Got event with id: {} and operation {}", messageId, operation);
        try {
            switch (operation) {
                case "requested":
                    orderRequestedEventHandler.handle(
                            new OrderRequested(null, messageId,record.timestamp(), objectMapper.readValue(record.value(), OrderRequestedDTO.class)));
                    break;
                case "confirmed":
                    orderConfimedEventEventHandler.handle(
                            new OrderConfirmed(null, messageId, record.timestamp(), objectMapper.readValue(record.value(), OrderConfirmedDTO.class)));
                    break;
                default:
                    log.error("Unsupported operation: {}", operation);
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing message with id: {} value: {}", messageId, record.value());
            log.error(e.getMessage());
        }
    }
}