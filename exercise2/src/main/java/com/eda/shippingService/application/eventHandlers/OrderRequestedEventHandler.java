package com.eda.shippingService.application.eventHandlers;

import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.domain.dto.incoming.ShipmentContentsDTO;
import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.entity.ProcessedMessage;
import com.eda.shippingService.domain.events.OrderRequested;
import com.eda.shippingService.adapters.repo.IdempotentHandlerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderRequestedEventHandler implements EventHandler<OrderRequested> {
    private final IdempotentHandlerRepository idempotentHandlerRepository;
    private final ShipmentService shipmentService;

    @Autowired
    public OrderRequestedEventHandler(IdempotentHandlerRepository idempotentHandlerRepository, ShipmentService shipmentService) {
        this.idempotentHandlerRepository = idempotentHandlerRepository;
        this.shipmentService = shipmentService;
    }

    @Transactional
    public void handle(OrderRequested event) {
        log.info("Handling OrderConfirmedEvent with ID: {}", event.getMessageId());
        if (idempotentHandlerRepository.findByMessageIdAndHandlerName(event.getMessageId(), this.getClass().getSimpleName()).isPresent()) {
            log.info("OrderConfirmedEvent with ID: {} already processed", event.getMessageId());
            return;
        }
        // Transforming this from event to DTO seems counterintuitive,
        // should the handler support taking events directly?
        shipmentService.provideRequestedContents(event.getMessageValue().orderId(),new ShipmentContentsDTO(
                event.getMessageValue().customerId(),
                event.getMessageValue().products().stream().map(product -> new OrderLineItemDTO(product.productId(), product.quantity())).toList()));
        idempotentHandlerRepository.save(new ProcessedMessage(event.getMessageId(), this.getClass().getSimpleName()));
    }
}
