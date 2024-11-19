package com.eda.shippingService.adapters.eventing;

import com.eda.shippingService.application.commandHandlers.BoxShipmentHandler;
import com.eda.shippingService.application.commandHandlers.ShipmentAddressSelectedHandler;
import com.eda.shippingService.domain.commands.BoxShipment;
import com.eda.shippingService.domain.commands.ShipmentAddressSelected;
import com.eda.shippingService.domain.dto.incoming.BoxShipmentDTO;
import com.eda.shippingService.domain.dto.incoming.SelectShipmentAddressDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
@Slf4j
public class KafkaCommandListener {
    private final BoxShipmentHandler boxShipmentHandler;
    private final ShipmentAddressSelectedHandler shipmentAddressSelectedHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    public KafkaCommandListener(BoxShipmentHandler boxShipmentHandler, ShipmentAddressSelectedHandler shipmentAddressSelectedHandler) {
        this.boxShipmentHandler = boxShipmentHandler;
        this.shipmentAddressSelectedHandler = shipmentAddressSelectedHandler;
    }

    @KafkaListener(topics = "${kafka.topic.commands}")
    public void listenToCommands(ConsumerRecord<String, String> record) {
        var headers = record.headers().toArray();
        var operation = Arrays.stream(headers).filter(header -> header.key().equals("operation")).findFirst().map(header -> new String(header.value())).orElseThrow();
        var messageId = UUID.fromString(Arrays.stream(headers).filter(header -> header.key().equals("messageId")).findFirst().map(header -> new String(header.value())).orElseThrow());
        try {
            switch (operation) {
                case "boxShipment":
                    boxShipmentHandler.handle(new BoxShipment(
                            messageId,
                            record.timestamp(),
                            objectMapper.readValue(record.value(), BoxShipmentDTO.class)));
                    break;
                case "selectShippingAddress":
                    shipmentAddressSelectedHandler.handle(
                            new ShipmentAddressSelected(
                                    messageId,
                                    record.timestamp(),
                                    objectMapper.readValue(record.value(), SelectShipmentAddressDTO.class)));
                default:
                    throw new IllegalArgumentException("Unsupported operation: " + operation);
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing message with id: {} value: {}", messageId, record.value());
            log.error(e.getMessage());
        }
    }
}
