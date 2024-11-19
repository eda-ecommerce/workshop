package com.eda.ballpit.adapters.eventing;

import com.eda.ballpit.application.service.BallService;
import com.eda.ballpit.domain.entity.Ball;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class KafkaBallListener {

    public CountDownLatch testLatch = new CountDownLatch(1);
    public CountDownLatch testLatch2 = new CountDownLatch(1);
    public CountDownLatch testLatch3 = new CountDownLatch(1);

    private final BallService ballService;

    @Autowired
    public KafkaBallListener(BallService ballService) {
        this.ballService = ballService;
    }

    @KafkaListener(topics = {"ball-json","ball-string"}, groupId = "simple-ball-consumer")
    public void listen(ConsumerRecord<String, String> record){
        log.info("Got ball: {}", record.value());
        Ball ball = new Ball(record.value());
        ballService.saveBall(ball);
        testLatch.countDown();
    }

    @KafkaListener(topics = "ball", groupId = "ball-json")
    public void listenJson(ConsumerRecord<String, String> record){
        var objectMapper = new ObjectMapper();
        log.info("Got ball: {}", record.value());
        try {
            Ball ball = objectMapper.readValue(record.value(), Ball.class);
            log.info("Ball object: {}", ball.toString());
            ballService.saveBall(ball);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        testLatch2.countDown();
    }
}
