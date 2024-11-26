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

    //TODO Implement the listenToColor method
    // Use the @KafkaListener annotation to listen to the topic "ball-color"
    // Use a String, or appropriate ConsumerRecord to get the color from the topic
    // Log anything you deem important (log.info,error,warn)
    public void listenToColor() {
        //TODO If the color is red, call the ballService to save a red ball
        // Otherwise do nothing (or log it if you want)
    }
}
