package com.eda.ballpit.adapters.eventing;

import com.eda.ballpit.domain.entity.Ball;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BallProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, Ball> dontUseMeKafkaTemplate;

    @Autowired
    public BallProducer(KafkaTemplate<String, String> kafkaTemplate, KafkaTemplate<String, Ball> dontUseMeKafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.dontUseMeKafkaTemplate = dontUseMeKafkaTemplate;
    }
    
    public void produceBallString(Ball ball){
        //TODO Use the kafkaTemplate to sent the color of the ball to the topic "ball-color"
    }

    public void produceBallJson(Ball ball){
        log.info("Producing ball json: {}", ball);
        dontUseMeKafkaTemplate.send("ball-json", ball);
    }
}
