package com.eda.ballpit.eventing;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.converters.DateTimeConverter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Getter
@Slf4j
@SpringBootTest
@EmbeddedKafka(topics = {"ball-color", "ball-json"}, kraft = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SuppressWarnings({"LoggingSimilarMessage"})
public abstract class KafkaTest {
	// For consuming
	@Getter
	private static final ArrayList<ConsumerRecord<String, String>> consumedShipmentRecords = new ArrayList<>();
	@Getter
	private static final ArrayList<ConsumerRecord<String, String>> ballColorRecords = new ArrayList<>();
	@Getter
	private static final ArrayList<ConsumerRecord<String, String>> ballJsonRecords = new ArrayList<>();

	public final static ResettableCountDownLatch ballColorListenerLatch = new ResettableCountDownLatch(1);
	public final static ResettableCountDownLatch ballJsonListenerLatch = new ResettableCountDownLatch(1);

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory;

	private static ConcurrentMessageListenerContainer<String, String> dummyContainer;

	@BeforeEach
	void setUpEach() {
		log.info("General Reset");
		dummyContainer = kafkaListenerContainerFactory.createContainer("ball-color", "ball-json");
		dummyContainer.setupMessageListener(new DummyMessageListener());
		dummyContainer.start();
		ContainerTestUtils.waitForAssignment(dummyContainer, 2);
		ballColorListenerLatch.reset();
		ballJsonListenerLatch.reset();
		consumedShipmentRecords.clear();
		ballJsonRecords.clear();
		ballColorRecords.clear();
	}

	@AfterEach
	void tearDownEach() {
		log.info("General Teardown");
		dummyContainer.stop();
	}

	ConsumerRecord<String, String> processRecord(ConsumerRecord<String, String> record){
		log.info("Received message from topic: {}", record.topic());
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

	ConsumerRecord<String, String> processStringRecord(ConsumerRecord<String, String> record){
		Instant instant = Instant.ofEpochSecond(record.timestamp());
		var time = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		log.info("Received message from topic: {} at: {}", record.topic(), time);
		log.info("Partition: {}", record.partition());
		log.info("---- Payload ----");
		log.info(record.value());
		log.info("-----------------");
		return record;
	}

	@KafkaListener(topics = {"ball-color"}, groupId = "ball-color-test")
	void ballColorListener(ConsumerRecord<String, String> record){
		log.info("Processing Test record from topic: {}", record.topic());
		ballColorRecords.add(processStringRecord(record));
		ballColorListenerLatch.countDown();
	}

	@KafkaListener(topics = {"ball-json"}, groupId = "ball-json-test")
	void ballJsonListener(ConsumerRecord<String, String> record){
		ballJsonRecords.add(processRecord(record));
		ballJsonListenerLatch.countDown();
	}
}