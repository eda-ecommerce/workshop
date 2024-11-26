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

//TODO Listen to the topics: order, product, offering, shopping-basket
@Component
public class KafkaMultiTopicListener {

    private final Logger log = LoggerFactory.getLogger(KafkaMultiTopicListener.class);

    //TODO Listen to the order topic
    // Use the @KafkaListener annotation to listen to the topic "order",
    // Pass your container factory by method name in the containerFactory parameter
    //Use the @Payload annotation in the Parameter to expect an OrderDTO
    //For the bonus: @Headers is your friend, it will give you a Map of Strings and Objects
     public void listenToOrderTopic() {
        //TODO log the Payload to log.info
        //TODO Bonus: log all headers to log.info
        // Check if you did this correctly by running the tests under com.eda.shippingService.exercise2
    }

    //TODO Listen to the other topics by copying your method and
    // changing the topic name, data type and method name
    //TODO: Product topic (ProductDTO)
    //TODO: Offering topic (OfferingDTO)
    //TODO: ShoppingBasket topic (ShoppingBasketDTO)
}