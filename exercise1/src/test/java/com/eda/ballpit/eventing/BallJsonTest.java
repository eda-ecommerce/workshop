package com.eda.ballpit.eventing;

import com.eda.ballpit.adapters.eventing.BallJsonListener;
import com.eda.ballpit.adapters.eventing.BallProducer;
import com.eda.ballpit.adapters.repo.BallRepository;
import com.eda.ballpit.application.service.BallService;
import com.eda.ballpit.domain.entity.Ball;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@Slf4j
public class BallJsonTest extends KafkaTest {
    private final BallService ballService;
    private final KafkaTemplate<String, String> stringTemplate;
    private final KafkaTemplate<String, Object> anyTemplate;
    private final BallRepository ballRepository;
    private final ObjectMapper objectMapper;
    private final BallProducer ballProducer;

    @Autowired
    public BallJsonTest(BallService ballService, KafkaTemplate<String, String> stringTemplate, KafkaTemplate<String, Object> anyTemplate, BallRepository ballRepository, BallProducer ballProducer) {
        this.ballService = ballService;
        this.stringTemplate = stringTemplate;
        this.anyTemplate = anyTemplate;
        this.ballRepository = ballRepository;
        this.ballProducer = ballProducer;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @BeforeEach
    void setUpEach(){
        super.setUpEach();
        ballRepository.deleteAll();
    }

    @Test
    void shouldThrowRedBall() throws InterruptedException, IOException {
        ballProducer.produceBallJson(new Ball("red"));
        assertTrue(ballJsonListenerLatch.await(3, TimeUnit.SECONDS));
        assertEquals(1,getBallJsonRecords().size());
        var consumedBall = objectMapper.readValue(getBallJsonRecords().get(0).value(), Ball.class);
        assertEquals("red", consumedBall.getColor());
    }

    @Test
    void shouldSaveRedBall() throws InterruptedException {
        Thread.sleep(2000);
        anyTemplate.send(MessageBuilder
                .withPayload(new Ball("red"))
                .setHeader(KafkaHeaders.TIMESTAMP, System.currentTimeMillis())
                .setHeader(KafkaHeaders.TOPIC, "ball-json").build());
        waitAtMost(5, TimeUnit.SECONDS).untilAsserted(
                () -> {
                    var list = StreamSupport.stream(ballRepository.findAll().spliterator(), false).toList();
                    assertEquals(1, list.size(), "There should only be one entry in the database");
                    assertEquals("red", list.get(0).getColor(), "The only color of the ball should be red");
                }
        );
    }

    @Test
    void shouldNotSavePinkBall() throws InterruptedException {
        stringTemplate.send("ball-json", """
                                {
                                "id": "00000000-0000-0000-0000-000000000123",
                                "color": "pink"
                                }
                """);
        Thread.sleep(5000);
        var list = StreamSupport.stream(ballRepository.findAll().spliterator(), false).toList();
        assertEquals(0, list.size());
    }


}
