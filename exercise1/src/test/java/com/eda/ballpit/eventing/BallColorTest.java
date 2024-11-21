package com.eda.ballpit.eventing;

import com.eda.ballpit.adapters.eventing.BallColorListener;
import com.eda.ballpit.adapters.repo.BallRepository;
import com.eda.ballpit.application.service.BallService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BallColorTest extends KafkaTest {
    private final BallService ballService;
    private final KafkaTemplate<String, String> stringTemplate;
    private final BallRepository ballRepository;
    private final BallColorListener ballColorListener;

    @Override
    @BeforeEach
    void setUpEach(){
        super.setUpEach();
        log.warn("Resetting");
        ballRepository.deleteAll();
        ballColorListener.reset();
    }

    @Autowired
    public BallColorTest(BallService ballService, KafkaTemplate<String, String> stringTemplate, BallRepository ballRepository, BallColorListener ballColorListener) {
        this.ballService = ballService;
        this.stringTemplate = stringTemplate;
        this.ballRepository = ballRepository;
        this.ballColorListener = ballColorListener;
    }

    @Test
    void shouldThrowRedBall() throws InterruptedException {
        this.ballService.throwBall("red");
        assertTrue(ballColorListenerLatch.await(3 , TimeUnit.SECONDS));
        assertEquals(1, getBallColorRecords().size());
        assertEquals("red",getBallColorRecords().get(0).value());
    }

    @Test
    void shouldSaveRedBall() throws InterruptedException {
        stringTemplate.send("ball-color", "red");
        assertTrue(ballColorListener.getTestLatch().await(5, TimeUnit.SECONDS));
        var list = StreamSupport.stream(ballRepository.findAll().spliterator(), false).toList();
        assertEquals(1, list.size());
        assertEquals("red", list.get(0).getColor());
    }

    @Test
    void shouldNotSavePinkBall() throws InterruptedException {
        stringTemplate.send("ball-color", "pink");
        ballColorListenerLatch.await(3, TimeUnit.SECONDS);
        var list = StreamSupport.stream(ballRepository.findAll().spliterator(), false).toList();
        assertEquals(0, list.size());
    }
}
