package com.eda.shippingService.exercise2;

import com.eda.shippingService.LogCapture;
import com.eda.shippingService.LogCaptureExtension;
import com.eda.shippingService.domain.dto.incoming.OrderDTO;
import com.eda.shippingService.eventing.KafkaTest;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static com.eda.shippingService.TestHelpers.quickUUID;
import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ConsumeOrderRequestedCustomMessageTest extends KafkaTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @RegisterExtension
    static LogCaptureExtension logCaptureExtension = new LogCaptureExtension("com.eda.shippingService.adapters.eventing");

    private final String orderRequestedPayload = FileUtils.readFileToString(new File("src/test/java/com/eda/shippingService/eventing/data/given/orderRequested.json"), StandardCharsets.UTF_8);

    public ConsumeOrderRequestedCustomMessageTest() throws IOException {
    }

    @Test
    void consumeOrderRequestedMessage(LogCapture logCapture) throws IOException, InterruptedException {
        Thread.sleep(3000);
        logCapture.setLogFilter(Level.INFO);
        var record = new ProducerRecord<String, String>("order", orderRequestedPayload);
        record.headers().add("operation", "requested".getBytes(StandardCharsets.UTF_8));
        record.headers().add("messageId", quickUUID(123456).toString().getBytes(StandardCharsets.UTF_8));
        var orderRequestedObj = new ObjectMapper().readValue(orderRequestedPayload, OrderDTO.class);
        kafkaTemplate.send(record);
        waitAtMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                            var logMessages = logCapture.getLoggingEvents();
                            assertTrue(!logMessages.isEmpty(), "Expected log messages to be present");
                            assertTrue(logMessages.stream().anyMatch(e -> e.getFormattedMessage().contains(orderRequestedObj.toString())), "Expected log message to contain order requested payload");
                        });
    }

    @Test
    void bonusPrintAllHeaders(LogCapture logCapture) throws InterruptedException {
        Thread.sleep(5000);
        logCapture.setLogFilter(Level.INFO);
        var record = new ProducerRecord<String, String>("order", orderRequestedPayload);
        record.headers().add("operation", "requested".getBytes(StandardCharsets.UTF_8));
        record.headers().add("messageId", quickUUID(123456).toString().getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(record);
        waitAtMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    var logMessages = logCapture.getLoggingEvents();
                    assertTrue(!logMessages.isEmpty(), "Expected log messages to be present");
                    assertTrue(logMessages.stream().anyMatch(e -> e.getFormattedMessage().contains("operation")), "Expected log message to operation header");
                    assertTrue(logMessages.stream().anyMatch(e -> e.getFormattedMessage().contains("messageId")), "Expected log message to contain messageId header");
                    assertTrue(logMessages.stream().anyMatch(e -> e.getFormattedMessage().contains("requested")), "Expected log message to contain the specific operation value");
                    assertTrue(logMessages.stream().anyMatch(e -> e.getFormattedMessage().contains(quickUUID(123456).toString())), "Expected log message to contain specific messageUUID");
                });
    }
}