package com.eda.shippingService.adapters.eventing;

import com.eda.shippingService.domain.dto.incoming.OfferingDTO;
import com.eda.shippingService.domain.dto.incoming.OrderDTO;
import com.eda.shippingService.domain.dto.incoming.ProductDTO;
import com.eda.shippingService.domain.dto.incoming.ShoppingBasketDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaMultiTopicListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger log = LoggerFactory.getLogger(KafkaMultiTopicListener.class);

    @KafkaListener(topics = "order", containerFactory = "kafkaListenerContainerFactoryJson")
    public void listenToOrderTopic(@Payload OrderDTO orderDTO, @Headers Map<String, Object> headers) {
        log.info("Consumed order: {}", orderDTO);
        for(String key : headers.keySet()) {
            log.info("Header: {} = {}", key, headers.get(key));
        }
    }

    @KafkaListener(topics = "product", containerFactory = "kafkaListenerContainerFactoryJson")
    public void listenToProductTopic(@Payload ProductDTO product) {
        log.info("Consumed product: {}", product);
    }

    @KafkaListener(topics = "offering", containerFactory = "kafkaListenerContainerFactoryJson")
    public void listenToOfferingTopic(@Payload OfferingDTO offering) {
        log.info("Consumed offering: {}", offering);
    }

    @KafkaListener(topics = "shopping-basket", containerFactory = "kafkaListenerContainerFactoryJson")
    public void listenToShoppingBasketTopic(@Payload ShoppingBasketDTO shoppingBasket) {
        log.info("Consumed shopping basket: {}", shoppingBasket);
    }
}