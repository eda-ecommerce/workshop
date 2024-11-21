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
    @Getter
    private CountDownLatch testLatch = new CountDownLatch(1);
    @Getter
    private final ArrayList<ConsumerRecord<String, String>> ballColorRecords = new ArrayList<>();

    private final BallService ballService;

    @Autowired
    public BallColorListener(BallService ballService) {
        this.ballService = ballService;
    }

    @KafkaListener(topics = {"ball-color"}, groupId = "ball-color")
    public void listenColor(ConsumerRecord<String, String> record){
        log.info("Got ball color: {}", record.value());
        if (record.value().equals("red")) {
            log.info("Saving ball with color: {}", record.value());
            ballService.saveBall(record.value());
        }
        //For testing purposes,
        testLatch.countDown();
        ballColorRecords.add(record);
    }

    public void reset(){
        ballColorRecords.clear();
        testLatch = new CountDownLatch(1);
    }
}
