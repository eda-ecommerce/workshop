package com.eda.shippingService.adapters.eventing.config;

import com.eda.shippingService.application.service.StockService;
import com.eda.shippingService.domain.dto.incoming.ProductDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class KafkaProductListener {
    private final StockService stockService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public KafkaProductListener(StockService stockService) {
        this.stockService = stockService;
    }

    @KafkaListener(topics = "${kafka.topic.product}")
    public void listenProduct(ConsumerRecord<String, String> record){
        //Process headers
        var headers = record.headers().toArray();
        var operation = Arrays.stream(headers)
                .filter(header -> header.key().equals("operation"))
                .findFirst()
                .map(header -> new String(header.value()))
                .orElseThrow();

        log.info("Got product event with operation {}", operation);
        try {
            if (operation.equals("created")) {
                var dto = objectMapper.readValue(record.value(), ProductDTO.class);
                stockService.registerNewProduct(dto.id(), 100);
            } else {
                log.warn("Product operation {} not tracked, skipping", operation);
            }
        }catch (JsonProcessingException e){
            log.error("Error processing product event", e);
            log.error("Payload: {}", record.value());
        }
    }
}
