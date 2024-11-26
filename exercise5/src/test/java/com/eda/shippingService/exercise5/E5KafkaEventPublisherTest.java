package com.eda.shippingService.exercise5;

import com.eda.shippingService.adapters.eventing.KafkaEventPublisher;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.eda.shippingService.domain.events.ShipmentRequested;
import com.eda.shippingService.helper.KafkaTest;
import com.eda.shippingService.helper.TestHelpers;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

import static com.eda.shippingService.helper.TestHelpers.quickUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class E5KafkaEventPublisherTest extends KafkaTest {
    @Autowired
    private KafkaEventPublisher kafkaEventPublisher;

    @Test
    void shouldPublish() throws InterruptedException, JsonProcessingException {
        var dtoBuilder = new TestHelpers.ShipmentDTOBuilder();
        var dto = dtoBuilder.withOrderId(quickUUID(123))
                .withStatus(ShipmentStatus.CONFIRMED)
                .withDestination("yeet")
                .withRequestedProduct(quickUUID(111), 10)
                .build();
        //when
        kafkaEventPublisher.publish(new ShipmentRequested(dto), "shipments");

        //then
        shipmentListenerLatch.await(5, TimeUnit.SECONDS);
        var found = getConsumedShipmentRecords().get(0);
        assertEquals(dto, found);
    }
}
