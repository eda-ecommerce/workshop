package com.eda.shippingService.adapters.eventing;

import com.eda.shippingService.domain.events.common.CustomMessage;

@SuppressWarnings("rawtypes")
public interface EventPublisher {
    void publish(CustomMessage event, String topic);
}
