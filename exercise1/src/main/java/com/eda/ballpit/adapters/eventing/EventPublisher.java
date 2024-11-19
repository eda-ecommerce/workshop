package com.eda.ballpit.adapters.eventing;

import com.eda.ballpit.domain.events.common.Message;

@SuppressWarnings("rawtypes")
public interface EventPublisher {
    void publish(Message event, String topic);
}
