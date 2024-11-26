package com.eda.ballpit.adapters.eventing;

import com.eda.ballpit.application.service.BallService;
import com.eda.ballpit.domain.entity.Ball;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BallJsonListener {

    private final BallService ballService;

    @Autowired
    public BallJsonListener(BallService ballService) {
        this.ballService = ballService;
    }

    @KafkaListener(topics = "ball-json", groupId = "ball-json", containerFactory = "kafkaListenerContainerFactoryJson")
    public void listenJson(@Payload Ball ball){
        log.info("Consumed ball json: {}", ball);
        if (ball.getColor().equals("red")) {
            ballService.catchBall(ball);
        } else {
            log.info("Dodging ball: {}", ball);
        }
    }
}
