package com.eda.ballpit.application.service;

import com.eda.ballpit.adapters.eventing.BallProducer;
import com.eda.ballpit.adapters.repo.BallRepository;
import com.eda.ballpit.domain.entity.Ball;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BallService {
    public final KafkaTemplate<String, String> eventPublisher;
    public final BallRepository ballPit;
    private final BallProducer ballProducer;
    @Autowired
    public BallService(KafkaTemplate<String, String> eventPublisher, BallRepository ballPit, BallProducer ballProducer) {
        this.eventPublisher = eventPublisher;
        this.ballPit = ballPit;
        this.ballProducer = ballProducer;
    }

    public void throwBall(String color){
        Ball ball = new Ball(color);
        ballProducer.produceBallColor(ball);
        ballProducer.produceBallJson(ball);
    }

    public void saveBall(String color){
        log.info("Saving ball with color: {}", color);
        Ball ball = new Ball(color);
        ballPit.save(ball);
    }

    public void saveBall(Ball ball){
        log.info("Saving ball: {}", ball);
        ballPit.save(ball);
    }

}
