package com.eda.shippingService.adapters.eventing;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.topic.shipment}")
    private String shipmentTopic;

    @Value(value = "${kafka.topic.stock}")
    private String stockTopic;

    @Value(value = "${kafka.topic.commands}")
    private String commandTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic shipmentTopic() {
        return new NewTopic(shipmentTopic, 1, (short) 1);
    }
    @Bean
    public NewTopic stockTopic() {
        return new NewTopic(stockTopic, 1, (short) 1);
    }
    @Bean
    public NewTopic commandTopic() {
        return new NewTopic(commandTopic, 1, (short) 1);
    }
}