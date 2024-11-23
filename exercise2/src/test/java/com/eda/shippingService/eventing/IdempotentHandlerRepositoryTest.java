package com.eda.shippingService.eventing;

import com.eda.shippingService.ShippingServiceApplication;
import com.eda.shippingService.application.eventHandlers.OrderRequestedEventHandler;
import com.eda.shippingService.domain.dto.incoming.OrderRequestedDTO;
import com.eda.shippingService.domain.entity.ProcessedMessage;
import com.eda.shippingService.domain.events.OrderRequested;
import com.eda.shippingService.adapters.repo.IdempotentHandlerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes =  {ShippingServiceApplication.class})
@DataJpaTest
@Slf4j
class IdempotentHandlerRepositoryTest {
    @Autowired
    private IdempotentHandlerRepository idempotentHandlerRepository;

    @Test
    void shouldFindEntry() {
        // Arrange
        UUID messageId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID orderId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        OrderRequestedDTO orderRequestedDTO = new OrderRequestedDTO(
            orderId,
            UUID.randomUUID(),
            "2021-09-01",
            "CONFIRMED",
            List.of(new OrderRequestedDTO.Product(UUID.randomUUID(), 1))
        );
        OrderRequested event = new OrderRequested(
                null, messageId, System.currentTimeMillis(), orderRequestedDTO
        );

        // Act
        idempotentHandlerRepository.save(new ProcessedMessage(event.getMessageId(), OrderRequestedEventHandler.class.getSimpleName()));

        // Assert
        assertTrue(idempotentHandlerRepository.findByMessageIdAndHandlerName(event.getMessageId(), OrderRequestedEventHandler.class.getSimpleName()).isPresent());
    }
}
