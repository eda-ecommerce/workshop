package com.eda.ballpit.eventing;

import com.eda.ballpit.adapters.eventing.BallColorListener;
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
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BallColorTest extends KafkaTest {
    private final KafkaTemplate<String, String> stringTemplate;
    private final BallRepository ballRepository;
    private final BallProducer ballProducer;

    @Override
    @BeforeEach
    void setUpEach(){
        super.setUpEach();
        log.warn("Resetting");
        ballRepository.deleteAll();
    }

    @Autowired
    public BallColorTest(BallService ballService, KafkaTemplate<String, String> stringTemplate, BallRepository ballRepository, BallProducer ballProducer) {
        this.stringTemplate = stringTemplate;
        this.ballRepository = ballRepository;
        this.ballProducer = ballProducer;
    }

    @Test
    void shouldThrowRedBall() throws InterruptedException {
        ballProducer.produceBallString(new Ball("red"));
        assertTrue(ballColorListenerLatch.await(3 , TimeUnit.SECONDS));
        assertEquals(1, getBallColorRecords().size());
        assertEquals("red",getBallColorRecords().get(0).value());
    }

    @Test
    void shouldSaveRedBall() {
        stringTemplate.send("ball-color", "red");
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
        stringTemplate.send("ball-color", "pink");
        Thread.sleep(5000);
        var list = StreamSupport.stream(ballRepository.findAll().spliterator(), false).toList();
        assertEquals(0, list.size());
    }

    @Test
    void shouldCatchALotOfBalls(){
        for(int i = 0; i < 100; i++){
            stringTemplate.send("ball-color", UUID.randomUUID().toString() ,"red");
        }
        waitAtMost(200, TimeUnit.SECONDS).untilAsserted(
                () -> {
                    var list = StreamSupport.stream(ballRepository.findAll().spliterator(), false).toList();
                    assertEquals(100, list.size(), "There should be 100 entries in the database");
                    for(var ball : list){
                        assertEquals("red", ball.getColor(), "The only color of the ball should be red");
                    }
                }
        );
    }
}
