package com.eda.ballpit.eventing;

import com.eda.ballpit.adapters.eventing.KafkaBallListener;
import com.eda.ballpit.adapters.repo.BallRepository;
import com.eda.ballpit.application.service.BallService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@DirtiesContext
@Slf4j
public class BallTest extends KafkaTest {
    private final BallService ballService;
    private final KafkaTemplate<String, String> stringTemplate;
    private final BallRepository ballRepository;
    private final KafkaBallListener ballListener;

    @Autowired
    public BallTest(BallService ballService, KafkaTemplate<String, String> stringTemplate, BallRepository ballRepository, KafkaBallListener ballListener) {
        this.ballService = ballService;
        this.stringTemplate = stringTemplate;
        this.ballRepository = ballRepository;
        this.ballListener = ballListener;
    }

    @Test
    void testThrow() throws InterruptedException {
        this.ballService.throwBall("red");
        ballListenerLatch.await(3, TimeUnit.SECONDS);
        assert super.getBallRecords().size() == 1;
        log.info("Ball received: {}", super.getBallRecords().get(0).value());
        ballRepository.findAll().forEach(
                ball -> {
                    assert ball.getColor().equals("red");
                }
        );
    }
    @Test
    void testStringThrow() throws InterruptedException {
        stringTemplate.send("ball", """
                                {
                                "id": "00000000-0000-0000-0000-000000000123",
                                "color": "red"
                                }
                """);
        ballListenerLatch.await();
        assert super.getBallRecords().size() == 1;
        log.info("Ball received: {}", super.getBallRecords().get(0).value());
        ballRepository.findAll().forEach(
                ball -> {
                    assert ball.getColor().equals("red");
                }
        );
    }
}
