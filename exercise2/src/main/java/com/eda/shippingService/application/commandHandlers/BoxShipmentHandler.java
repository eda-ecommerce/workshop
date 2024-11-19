package com.eda.shippingService.application.commandHandlers;

import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.domain.commands.BoxShipment;
import com.eda.shippingService.domain.dto.common.PackageDimensionsDTO;
import com.eda.shippingService.domain.dto.incoming.IncomingPackageDTO;
import com.eda.shippingService.domain.entity.ProcessedMessage;
import com.eda.shippingService.adapters.repo.IdempotentHandlerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoxShipmentHandler implements CommandHandler<BoxShipment> {
    private final ShipmentService shipmentService;
    private final IdempotentHandlerRepository idempotentHandlerRepository;

    @Autowired
    public BoxShipmentHandler(ShipmentService shipmentService, IdempotentHandlerRepository idempotentHandlerRepository) {
        this.shipmentService = shipmentService;
        this.idempotentHandlerRepository = idempotentHandlerRepository;
    }

    @Transactional
    public void handle(BoxShipment command){
        if(idempotentHandlerRepository.findByMessageIdAndHandlerName(command.getMessageId(), this.getClass().getSimpleName()).isPresent()){
            return;
        }
        var dto = new IncomingPackageDTO(
                new PackageDimensionsDTO(
                        command.getMessageValue().height(),
                        command.getMessageValue().width(),
                        command.getMessageValue().depth()
                ),
                command.getMessageValue().weight(),
                command.getMessageValue().contents()
        );
        shipmentService.boxShipment(command.getMessageValue().orderId(), dto);
        idempotentHandlerRepository.save(new ProcessedMessage(command.getMessageId(), this.getClass().getSimpleName()));
    }
}
