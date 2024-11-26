package com.eda.ballpit.adapters.eventing;

import com.eda.ballpit.application.service.BallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BallColorListener {
    private final BallService ballService;

    @Autowired
    public BallColorListener(BallService ballService) {
        this.ballService = ballService;
    }


    //TODO Implement the listenToColor method
    // Use the @KafkaListener annotation to listen to the topic "ball-color"
    // Use a String to get the color from the topic
    // If the color is red, call the ballService to save a red ball
    // Otherwise do nothing (or log it if you want)
    @KafkaListener(topics = "ball-color", groupId = "ball-color", concurrency = "5")
    public void listenToColor(@Payload String data) {
        if(data.equals("red")){
            ballService.catchBall(data);
        }
        else {
            log.info("Dodging ball: {}", data);
        }
    }
}
