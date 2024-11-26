package com.eda.ballpit.adapters.eventing;

import com.eda.ballpit.application.service.BallService;
import com.eda.ballpit.domain.entity.Ball;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BallJsonListener {

    private final BallService ballService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public BallJsonListener(BallService ballService) {
        this.ballService = ballService;
    }

    @KafkaListener(topics = "ball-json", groupId = "ball-json")
    public void listenJson(String data) {
        try {
            Ball ball = objectMapper.readValue(data, Ball.class);
            if (ball.getColor().equals("red")) {
                ballService.catchBall(ball);
            }
        } catch (Exception e) {
            log.error("Error while processing ball json", e);
        }
    }
}
