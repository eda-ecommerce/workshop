package com.eda.ballpit.eventing;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Getter
@Testcontainers
@Slf4j(topic = "TestKafkaListener")
@SpringBootTest
public abstract class KafkaTest {
	@Container
	static final KafkaContainer kafkaContainer = new KafkaContainer(
        DockerImageName.parse("apache/kafka:3.8.1")
	);

	// For consuming
	private ArrayList<ConsumerRecord<String, String>> consumedShipmentRecords = new ArrayList<>();
	private ArrayList<ConsumerRecord<String, String>> ballRecords = new ArrayList<>();
	public final static ResettableCountDownLatch ballListenerLatch = new ResettableCountDownLatch(1);
	public final static ResettableCountDownLatch shipmentListenerLatch = new ResettableCountDownLatch(1);

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@DynamicPropertySource
	static void kafkaProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
	}

	@BeforeEach
	void setUpEach() {
		ballListenerLatch.reset();
		shipmentListenerLatch.reset();
		consumedShipmentRecords = new ArrayList<>();
		ballRecords = new ArrayList<>();
	}

	@BeforeAll
    static void setUp() {
		kafkaContainer.start();
	}

	ConsumerRecord<String, String> processRecord(ConsumerRecord<String, String> record){
		log.info("Received message from topic: {}", record.topic());
		log.info("---- Headers ----");
		for (Header header : record.headers()) {
			log.info("K: "+header.key() +" |V: "+ new String(header.value(), StandardCharsets.UTF_8));
		}
		log.info("---- Payload ----");
        try {
            Object jsonObject = objectMapper.readValue(record.value(), Object.class);
			log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("-----------------");
		return record;
	}

	@KafkaListener(topics = {"ball"}, groupId = "ball-test")
	void listenerStock(ConsumerRecord<String, String> record){
		ballRecords.add(processRecord(record));
		ballListenerLatch.countDown();
	}

	@KafkaListener(topics = {"shipment"}, groupId = "shipment-test")
	void listenerShipment(ConsumerRecord<String, String> record){
		consumedShipmentRecords.add(processRecord(record));
		shipmentListenerLatch.countDown();
	}
}