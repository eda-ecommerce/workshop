package com.eda.shippingService.application.eventHandlers;

import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.domain.entity.ProcessedMessage;
import com.eda.shippingService.domain.events.OrderConfirmed;
import com.eda.shippingService.adapters.repo.IdempotentHandlerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderConfirmedEventHandler implements EventHandler<OrderConfirmed> {
    private final ShipmentService shipmentService;
    private final IdempotentHandlerRepository idempotentHandlerRepository;

    @Autowired
    public OrderConfirmedEventHandler(ShipmentService shipmentService, IdempotentHandlerRepository idempotentHandlerRepository) {
        this.shipmentService = shipmentService;
        this.idempotentHandlerRepository = idempotentHandlerRepository;
    }

    @Override
    @Transactional
    public void handle(OrderConfirmed event) {
        var found = idempotentHandlerRepository.findByMessageIdAndHandlerName(event.getMessageId(), this.getClass().getSimpleName());
        if (found.isPresent()) {
            return;
        }
        var payload = event.getMessageValue();
        shipmentService.approveShipment(payload.orderId());
        log.info("OrderConfirmedEvent with ID: {} handled", event.getMessageId());
        idempotentHandlerRepository.save(new ProcessedMessage(event.getMessageId(), this.getClass().getSimpleName()));
    }
}
