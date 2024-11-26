package com.eda.shippingService.helper;

import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Getter
@Slf4j
@SpringBootTest
@EmbeddedKafka(topics = {"order", "stock", "shipment", "product", "shipment-commands"}, partitions = 1, kraft = true)
@SuppressWarnings({"LoggingSimilarMessage"})
public abstract class KafkaTest {
	// For consuming
	@Getter
	private static final ArrayList<ShipmentDTO> consumedShipmentRecords = new ArrayList<>();
	@Getter
	private static final ArrayList<ConsumerRecord<String, String>> consumedStockRecords = new ArrayList<>();
	@Getter
	private static final ArrayList<ConsumerRecord<String, String>> consumedProductRecords = new ArrayList<>();

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
	@Getter
	private ConcurrentMessageListenerContainer<String, String> dummyContainer;

	@BeforeEach
	public void setUpEach() throws InterruptedException {
		log.info("General Reset");
		kafkaListenerContainerFactory.getContainerProperties().setGroupId("KafkaTestDummy");
		dummyContainer = kafkaListenerContainerFactory.createContainer(stockTopic, shipmentTopic, orderTopic, productTopic);
		dummyContainer.setupMessageListener(new DummyMessageListener());
		dummyContainer.setTopicCheckTimeout(10);
		var rebalance = new RebalanceListener();
		dummyContainer.getContainerProperties().setConsumerRebalanceListener(rebalance);
		dummyContainer.start();
		ContainerTestUtils.waitForAssignment(dummyContainer, 4);
		rebalance.getLatch().await();
		stockListenerLatch.reset();
		shipmentListenerLatch.reset();
		consumedShipmentRecords.clear();
		consumedStockRecords.clear();
		consumedProductRecords.clear();
	}

	@AfterEach
	public void tearDownEach() {
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

	@KafkaListener(topics = {"shipments"}, groupId = "test-shipment", containerFactory = "kafkaListenerContainerFactoryJson")
	void listenerShipment(@Payload ShipmentDTO shipmentDTO, @Headers Map<String, Object> headers){
		for(String key : headers.keySet()){
			log.info("Header: {} | Value: {}", key, headers.get(key));
		}
		consumedShipmentRecords.add(shipmentDTO);
		shipmentListenerLatch.countDown();
	}

	@KafkaListener(topics = {"product"}, groupId = "test-product")
	void listenerProduct(ConsumerRecord<String, String> record){
		consumedProductRecords.add(processRecord(record));
		productListenerLatch.countDown();
	}

	@KafkaListener(topics = {"test"}, groupId = "test-order", containerFactory = "kafkaListenerContainerFactory")
	void listenTest(ConsumerRecord<String, String> record){
		log.info("Received message from topic: {}", record.topic());
		log.info("---- Headers ----");
		for (Header header : record.headers()) {
			log.info("K: "+header.key() +" |V: "+ new String(header.value(), StandardCharsets.UTF_8));
		}
		log.info("---- Payload ----");
		log.info(record.value());
		log.info("-----------------");
	}
}