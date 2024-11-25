package com.eda.ballpit.adapters.eventing;

import com.eda.ballpit.application.service.BallService;
import com.eda.ballpit.domain.entity.Ball;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class BallColorListener {
    private final BallService ballService;

    @Autowired
    public BallColorListener(BallService ballService) {
        this.ballService = ballService;
    }

    @KafkaListener(topics = {"ball-color"}, groupId = "ball-color")
    //TODO listen to a String from the ball-color topic
    public void listenColor(){
        //TODO: Log the ball color
        //TODO: If the color is red, use the ballService to save a red Ball
    }
}
