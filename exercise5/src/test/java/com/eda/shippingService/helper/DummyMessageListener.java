package com.eda.shippingService.helper;

import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.Acknowledgment;

@NoArgsConstructor
public class DummyMessageListener implements MessageListener<String, String> {
    @Override
    public void onMessage(@NotNull ConsumerRecord<String, String> data) {

    }

    @Override
    public void onMessage(@NotNull ConsumerRecord<String, String> data, Acknowledgment acknowledgment) {
        MessageListener.super.onMessage(data, acknowledgment);
    }

    @Override
    public void onMessage(@NotNull ConsumerRecord<String, String> data, @NotNull Consumer<?, ?> consumer) {
        MessageListener.super.onMessage(data, consumer);
    }

    @Override
    public void onMessage(@NotNull ConsumerRecord<String, String> data, Acknowledgment acknowledgment, @NotNull Consumer<?, ?> consumer) {
        MessageListener.super.onMessage(data, acknowledgment, consumer);
    }
}
