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
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class BallJsonListener {

    @Getter
    private CountDownLatch testLatch = new CountDownLatch(1);
    @Getter
    private final ArrayList<ConsumerRecord<String, String>> ballJsonRecords = new ArrayList<>();

    private final BallService ballService;

    @Autowired
    public BallJsonListener(BallService ballService) {
        this.ballService = ballService;
    }

    @KafkaListener(topics = "ball-json", groupId = "ball-json")
    //TODO listen to a ConsumerRecord from the topic
    public void listenJson(){
        var objectMapper = new ObjectMapper();
        try {
            //TODO Use objectMapper to create a Ball object from the json string
            //TODO If the ball color is red, call the ballService to save a red ball directly from your new object
        } catch (JsonProcessingException e) {
            log.error("Error processing ball", e);
        }
        //Easiest way to test this sadly
        testLatch.countDown();
    }

    public void reset(){
        ballJsonRecords.clear();
        testLatch = new CountDownLatch(1);
    }
}
