package com.eda.shippingService.application.commandHandlers;

import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.domain.commands.ShipmentAddressSelected;
import com.eda.shippingService.domain.entity.ProcessedMessage;
import com.eda.shippingService.adapters.repo.IdempotentHandlerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShipmentAddressSelectedHandler implements CommandHandler<ShipmentAddressSelected> {
    private final ShipmentService shipmentService;
    private final IdempotentHandlerRepository idempotentHandlerRepository;

    @Autowired
    public ShipmentAddressSelectedHandler(ShipmentService shipmentService, IdempotentHandlerRepository idempotentHandlerRepository) {
        this.shipmentService = shipmentService;
        this.idempotentHandlerRepository = idempotentHandlerRepository;
    }

    @Override
    public void handle(ShipmentAddressSelected command) {
        if (idempotentHandlerRepository.findByMessageIdAndHandlerName(command.getMessageId(),this.getClass().getSimpleName()).isPresent()){
            return;
        }
        shipmentService.provideShippingAddress(command.getMessageValue().orderId(), command.getMessageValue().shippingAddress());
        idempotentHandlerRepository.save(new ProcessedMessage(command.getMessageId(), this.getClass().getSimpleName()));
    }
}
