package com.eda.ballpit.adapters.eventing;

import com.eda.ballpit.domain.entity.Ball;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BallProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, Ball> ballKafkaTemplate;

    @Autowired
    public BallProducer(KafkaTemplate<String, String> kafkaTemplate, KafkaTemplate<String, Ball> ballKafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.ballKafkaTemplate = ballKafkaTemplate;
    }
    
    public void produceBallString(Ball ball){
        kafkaTemplate.send("ball", ball.getColor());
    }

    public void produceBallJson(Ball ball){
        var objectMapper = new ObjectMapper();
        try {
            var json = objectMapper.writeValueAsString(ball);
            kafkaTemplate.send("ball-json", json);
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Stacktrace: {}", (Object[]) e.getStackTrace());
        }
    }

    public void produceBallObject(Ball ball){
        ballKafkaTemplate.send("ball-object", ball);
    }
}