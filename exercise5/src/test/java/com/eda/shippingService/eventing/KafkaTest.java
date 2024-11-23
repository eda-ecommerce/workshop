package com.eda.shippingService.eventing;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Getter
@Slf4j
@SpringBootTest
@EmbeddedKafka(topics = {"ball-color", "ball-json", "shipment"}, partitions = 1, kraft = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SuppressWarnings({"LoggingSimilarMessage"})
public abstract class KafkaTest {
	// For consuming
	@Getter
	private static final ArrayList<ConsumerRecord<String, String>> consumedShipmentRecords = new ArrayList<>();
	@Getter
	private static final ArrayList<ConsumerRecord<String, String>> consumedStockRecords = new ArrayList<>();
	@Getter
	private static final ArrayList<ConsumerRecord<String, String>> consumedProductRecords = new ArrayList<>();

    @Qualifier("shipmentTopic")
    @Autowired
    @Value("${kafka.topic.shipment}")
	private String shipmentTopic;
	@Value("${kafka.topic.stock}")
	private String stockTopic;
	@Value("${kafka.topic.commands}")
	private String commandTopic;
	@Value("${kafka.topic.product}")
	private String productTopic;
	@Value("${kafka.topic.order}")
	private String orderTopic;



	public final static ResettableCountDownLatch stockListenerLatch = new ResettableCountDownLatch(1);
	public final static ResettableCountDownLatch shipmentListenerLatch = new ResettableCountDownLatch(1);
	public final static ResettableCountDownLatch productListenerLatch = new ResettableCountDownLatch(1);

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory;

	private static ConcurrentMessageListenerContainer<String, String> dummyContainer;
	@BeforeEach
	void setUpEach() {
		log.info("General Reset");
		dummyContainer = kafkaListenerContainerFactory.createContainer(stockTopic, shipmentTopic);
		dummyContainer.setupMessageListener(new DummyMessageListener());
		dummyContainer.start();
		ContainerTestUtils.waitForAssignment(dummyContainer, 2);
		stockListenerLatch.reset();
		shipmentListenerLatch.reset();
		consumedShipmentRecords.clear();
		consumedStockRecords.clear();
		consumedProductRecords.clear();
	}

	@AfterEach
	void tearDownEach() {
		log.info("General Teardown");
		dummyContainer.stop();
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

	@KafkaListener(topics = {"stock"}, groupId = "test-stock")
	void listenerStock(ConsumerRecord<String, String> record){
		consumedStockRecords.add(processRecord(record));
		stockListenerLatch.countDown();
	}

	@KafkaListener(topics = {"shipments"}, groupId = "test-shipment")
	void listenerShipment(ConsumerRecord<String, String> record){
		consumedShipmentRecords.add(processRecord(record));
		shipmentListenerLatch.countDown();
	}

	@KafkaListener(topics = {"product"}, groupId = "test-product")
	void listenerProduct(ConsumerRecord<String, String> record){
		consumedProductRecords.add(processRecord(record));
		productListenerLatch.countDown();
	}
}