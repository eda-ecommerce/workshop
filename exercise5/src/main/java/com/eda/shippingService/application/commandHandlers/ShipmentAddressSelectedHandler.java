package com.eda.shippingService.application.commandHandlers;

import com.eda.shippingService.application.service.IdempotentcyService;
import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.domain.commands.ShipmentAddressSelected;
import com.eda.shippingService.domain.entity.ProcessedMessage;
import com.eda.shippingService.adapters.repo.IdempotentHandlerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShipmentAddressSelectedHandler implements CommandHandler<ShipmentAddressSelected> {
    private final ShipmentService shipmentService;
    private final IdempotentcyService idempotentcyService;

    @Autowired
    public ShipmentAddressSelectedHandler(ShipmentService shipmentService, IdempotentcyService idempotentcyService) {
        this.shipmentService = shipmentService;
        this.idempotentcyService = idempotentcyService;
    }

    @Override
    public void handle(ShipmentAddressSelected command) {
        if (idempotentcyService.hasBeenProcessed(command)) {
            return;
        }
        shipmentService.provideShippingAddress(command.getMessageValue().orderId(), command.getMessageValue().shippingAddress());
        idempotentcyService.saveProcessedMessage(command);
    }
}
