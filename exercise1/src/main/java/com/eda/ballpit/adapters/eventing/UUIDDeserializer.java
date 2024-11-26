package com.eda.ballpit.adapters.eventing;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

public class UUIDDeserializer implements Deserializer<UUID> {
    private final StringDeserializer stringDeserializer = new StringDeserializer();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        stringDeserializer.configure(configs, isKey);
    }

    @Override
    public UUID deserialize(String topic, byte[] data) {
        return UUID.fromString(stringDeserializer.deserialize(topic, data));
    }

    @Override
    public UUID deserialize(String topic, Headers headers, byte[] data) {
        return UUID.fromString(stringDeserializer.deserialize(topic, headers, data));
    }

    @Override
    public UUID deserialize(String topic, Headers headers, ByteBuffer data) {
        return UUID.fromString(stringDeserializer.deserialize(topic, headers, data));
    }

    @Override
    public void close() {
        stringDeserializer.close();
    }
}
