package com.eda.shippingService.adapters.eventing;

import com.eda.shippingService.domain.events.common.Message;

@SuppressWarnings("rawtypes")
public interface EventPublisher {
    void publish(Message event, String topic);
}
