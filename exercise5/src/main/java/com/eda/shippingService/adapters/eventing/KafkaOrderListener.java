package com.eda.shippingService.adapters.eventing;

import com.eda.shippingService.application.service.IdempotentcyService;
import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.dto.incoming.OrderConfirmedDTO;
import com.eda.shippingService.domain.dto.incoming.OrderPayload;
import com.eda.shippingService.domain.dto.incoming.ShipmentContentsDTO;
import com.eda.shippingService.domain.events.OrderConfirmed;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
@Component
@Slf4j
public class KafkaOrderListener {

    private final ShipmentService shipmentService;
    private final IdempotentcyService idempotentcyService;

    @Autowired
    public KafkaOrderListener(ShipmentService shipmentService, IdempotentcyService idempotentcyService) {
        this.shipmentService = shipmentService;
        this.idempotentcyService = idempotentcyService;
    }
    //This should probably more fine-grained
    @KafkaListener(topics = "${kafka.topic.order}", containerFactory = "kafkaListenerContainerFactoryJson")
    public void listen(@Payload OrderPayload payload,
                       @Header("operation") String operation,
                       @Header("messageId") String messageId) {
        log.info("Got {} event with id {} and payload {}", operation, messageId, payload);
        try {
            switch (operation) {
                case "requested":
                    //TODO: Handle the OrderRequested event
                    //  Extract the necessary data into a new ShipmentContentsDTO
                    //  Or: Create a new Method in the ShipmentService that can handle the OrderPayload directly
                    // Call the shipmentService with the DTO and continue there
                    //TODO Bonus: make the listener idempotent by using the IdempotentcyService
                    break;
                case "confirmed":
                    //TODO: Implement the OrderConfirmed event
                    // Call the shipmentService with the required parameters
                    break;
                default:
                    log.error("Unsupported operation: {}", operation);
            }
        } catch (Exception e) {
            log.error("Error processing message with id: {} value: {}", messageId, payload);
            log.error(e.getMessage());
        }
    }
}