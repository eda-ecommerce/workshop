package com.eda.shippingService.adapters.eventing;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger log = LoggerFactory.getLogger(KafkaOrderListener.class);

    public void listenToOrderTopic() {
        //TODO: mark function as kafka listener with correct topic
        //TODO: Choose a suitable parameter type for the method
        //TODO: Log the actual json value of the consumer record (log.info)
        //TODO: Bonus Exercise: Log all header keys and their values of the received record
        //TODO: Bonus Exercise: Convert the json value to an OrderRequestedDTO object
    }
}