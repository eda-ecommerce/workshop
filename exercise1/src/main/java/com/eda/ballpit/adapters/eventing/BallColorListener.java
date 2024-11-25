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

    @KafkaListener(topics = {"ball-color"}, groupId = "ball-color")
    //TODO Implement the listenToColor method
    // Use a String to get the color from the topic
    // If the color is red, call the ballService to save a red ball
    // Otherwise to nothing (or log it maybe)
    public void listenToColor(){
    }
}
