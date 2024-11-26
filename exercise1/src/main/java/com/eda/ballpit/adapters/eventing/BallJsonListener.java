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
    //TODO Implement the listenJson method
    // Use the @KafkaListener annotation to listen to the topic "ball-json"
    // Use a String to get the json from the topic
    public void listenJson() {
        //TODO Use the objectMapper to convert the json to a Ball object
        // Check if the color ob the object is red if so,
        // Call the ballService to save the ball
    }
}
