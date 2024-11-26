package com.eda.shippingService.helper;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;

import java.util.Collection;

@Slf4j
public class RebalanceListener implements ConsumerRebalanceListener {
    @Getter
    private ResettableCountDownLatch latch = new ResettableCountDownLatch(1);

    @Bean
    public ConsumerRebalanceListener consumerRebalanceListener() {
        return new RebalanceListener();
    }
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        log.warn("Partitions revoked: {}", partitions);
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        log.info("Partitions assigned: {}", partitions);
        log.info("Counting down latch");
        latch.countDown();
    }

    @Override
    public void onPartitionsLost(Collection<TopicPartition> partitions) {
        ConsumerRebalanceListener.super.onPartitionsLost(partitions);
    }
}
