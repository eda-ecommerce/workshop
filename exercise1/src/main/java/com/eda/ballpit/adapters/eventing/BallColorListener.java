package com.eda.ballpit.adapters.eventing;

import com.eda.ballpit.application.service.BallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BallColorListener {
    private final BallService ballService;

    @Autowired
    public BallColorListener(BallService ballService) {
        this.ballService = ballService;
    }

    @KafkaListener(topics = "ball-color")
    public void listenToColor(String data) {
        if (data.equals("red")) {
            ballService.catchBall(data);
        } else {
            log.info("Received a ball of color: {}", data);
        }
    }
}
