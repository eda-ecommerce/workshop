package com.eda.shippingService.eventing;

import com.eda.shippingService.adapters.repo.ProductRepository;
import com.eda.shippingService.adapters.repo.ShipmentRepository;
import com.eda.shippingService.domain.entity.Address;
import com.eda.shippingService.domain.entity.OrderLineItem;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import jakarta.transaction.Transactional;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.eda.shippingService.TestHelpers.*;
import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.jupiter.api.Assertions.*;

public class FullIntegrationTest extends KafkaTest{
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ShipmentRepository shipmentRepository;

    private final String orderRequestedPayload = FileUtils.readFileToString(new File("src/test/java/com/eda/shippingService/eventing/data/given/orderRequested.json"), StandardCharsets.UTF_8);

    private final String productCreatedString = FileUtils.readFileToString(new File("src/test/java/com/eda/shippingService/eventing/data/given/productCreated.json"), StandardCharsets.UTF_8);

    private final String addressSelectedString = FileUtils.readFileToString(new File("src/test/java/com/eda/shippingService/eventing/data/given/shippingAddressSelected.json"), StandardCharsets.UTF_8);

    private final String orderConfirmedString = FileUtils.readFileToString(new File("src/test/java/com/eda/shippingService/eventing/data/given/orderConfirmed.json"), StandardCharsets.UTF_8);

    private final String boxShipmentString = FileUtils.readFileToString(new File("src/test/java/com/eda/shippingService/eventing/data/given/boxShipment.json"), StandardCharsets.UTF_8);


    public FullIntegrationTest() throws IOException {
    }

    @Transactional
    @Test
    void shouldRequestOrder() {
        //produce product event
        var record = new ProducerRecord<String, String>("product", productCreatedString);
        record.headers().add("operation", "created".getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(record);
        //Wait for product to be created
        waitAtMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertTrue(productRepository.findById(quickUUID(1234567)).isPresent()));        //produce order event
        var orderRecord = new ProducerRecord<String, String>("order", orderRequestedPayload);
        orderRecord.headers().add("operation", "requested".getBytes(StandardCharsets.UTF_8));
        orderRecord.headers().add("messageId", quickUUID(123456).toString().getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(orderRecord);
        //Wait for order to be created
        waitAtMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> assertTrue(shipmentRepository.findById(quickUUID(123)).isPresent()));
        var expectedShipment = new Shipment(
                quickUUID(123),
                null,
                null,
                List.of(new OrderLineItem(quickUUID(1234567), 10)),
                ShipmentStatus.INCOMPLETE
        );
        expectedShipment.reserve();
        //assert
        assertShipmentsEqual(expectedShipment, shipmentRepository.findById(quickUUID(123)).get());
        var addressSelectedRecord = new ProducerRecord<String, String>("shipment-commands", addressSelectedString);
        addressSelectedRecord.headers().add("operation", "selectShippingAddress".getBytes(StandardCharsets.UTF_8));
        addressSelectedRecord.headers().add("messageId", quickUUID(456).toString().getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(addressSelectedRecord);
        waitAtMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    var result2 = shipmentRepository.findById(quickUUID(123)).get();
                    var expectedAddress = new Address(
                            "Lustige Straße 123",
                            "Spaßhausen",
                            "Freistaat Lachen",
                            "12345",
                            "DE"
                    );
                    assertEquals(expectedAddress, result2.getDestination());
                });
        //Send orderConfirmed event
        var orderConfirmedRecord = new ProducerRecord<String, String>("order", orderConfirmedString);
        orderConfirmedRecord.headers().add("operation", "confirmed".getBytes(StandardCharsets.UTF_8));
        orderConfirmedRecord.headers().add("messageId", quickUUID(789).toString().getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(orderConfirmedRecord);
        //Wait for shipment to be confirmed
        waitAtMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    var result = shipmentRepository.findById(quickUUID(123)).get();
                    assertEquals(ShipmentStatus.CONFIRMED, result.getStatus());
                });
        //send box shipment command
        var boxShipmentRecord = new ProducerRecord<String, String>("shipment-commands", boxShipmentString);
        boxShipmentRecord.headers().add("operation", "boxShipment".getBytes(StandardCharsets.UTF_8));
        boxShipmentRecord.headers().add("messageId", quickUUID(456).toString().getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(boxShipmentRecord);
        //Wait for shipment to be boxed
        waitAtMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    var result = shipmentRepository.findById(quickUUID(123)).get();
                    assertEquals(ShipmentStatus.PACKAGED, result.getStatus());
                });
    }
}
