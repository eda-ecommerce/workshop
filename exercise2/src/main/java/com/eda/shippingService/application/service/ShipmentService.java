package com.eda.shippingService.application.service;

import com.eda.shippingService.adapters.eventing.EventPublisher;
import com.eda.shippingService.adapters.repo.PackageRepository;
import com.eda.shippingService.application.service.exception.IncompleteContentException;
import com.eda.shippingService.application.service.exception.NotEnoughStockException;
import com.eda.shippingService.domain.dto.incoming.IncomingPackageDTO;
import com.eda.shippingService.domain.dto.incoming.ShipmentContentsDTO;
import com.eda.shippingService.domain.dto.incoming.UpdateShipmentStatusDTO;
import com.eda.shippingService.domain.dto.common.AddressDTO;
import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.entity.OrderLineItem;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.eda.shippingService.adapters.repo.ShipmentRepository;
import com.eda.shippingService.domain.events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final StockService stockService;
    @Value("${kafka.topic.shipment}")
    private String shipmentTopic;

    @Autowired
    public ShipmentService(ShipmentRepository shipmentRepository, StockServiceImpl stockService) {
        this.shipmentRepository = shipmentRepository;
        this.stockService = stockService;
    }

    //it would be so easy to confuse destination and origin, should there be separate data types?
    public ShipmentDTO provideShippingAddress(UUID orderId, AddressDTO destination) {
        var found = shipmentRepository.findById(orderId).orElse(
                new Shipment(orderId, destination.toEntity(), null, null, ShipmentStatus.INCOMPLETE)
        );
        found.setDestination(destination.toEntity());
        shipmentRepository.save(found);
        return ShipmentDTO.fromEntity(found);
    }

    public void approveShipment(UUID orderId) {
        var found = shipmentRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order with id " + orderId + " does not exist"));
        found.approve();
        shipmentRepository.save(found);
    }

    public ShipmentDTO provideRequestedContents(UUID orderId, ShipmentContentsDTO shipmentDTO) {
        Shipment shipmentEntity = shipmentRepository.findById(orderId).orElse(shipmentDTO.toEntity(orderId));
        //Could be handled differently if we catch each reserve stock call and set status to partially complete or something / send out a smaller package
        try {
            for (var item : shipmentEntity.getRequestedProducts()) {
                stockService.reserveStock(item.productId(), item.quantity());
            }
            shipmentEntity.reserve();

            shipmentRepository.save(shipmentEntity);
            return ShipmentDTO.fromEntity(shipmentEntity);
        } catch (NotEnoughStockException e) {
            shipmentEntity.setStatus(ShipmentStatus.ON_HOLD);

            shipmentRepository.save(shipmentEntity);
            return ShipmentDTO.fromEntity(shipmentEntity);
        }
    }

    //This assumes that we call this with a complete package, maybe by an external working system. Idk if thats too much assumption
    //Otherwise call this with add content, but that would maybe justify a package service + reversal of dependencies for package
    public ShipmentDTO boxShipment(UUID orderId, IncomingPackageDTO packageDTO) throws IncompleteContentException {
        var found = shipmentRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order with id " + orderId + " does not exist"));
        var aPackage = packageDTO.toPackage();
        for (OrderLineItem item : aPackage.getContents()) {
            stockService.decreaseAndReleaseStock(item.productId(), item.quantity());
        }
        found.addPackage(aPackage);
        shipmentRepository.save(found);

        return ShipmentDTO.fromEntity(found);
    }

    public boolean deleteShipment(UUID orderId) {
        var found = shipmentRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order with id " + orderId + " does not exist"));
        shipmentRepository.delete(found);
        return true;
        // We ignore, that the package might be unpacked again and added back to the stockpile
    }

    public ShipmentDTO sendShipment(UUID orderId) {
        var found = shipmentRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order with id " + orderId + " does not exist"));
        //Notify external shipping company / driver and assign tracking number
        //This is obviously very mocked
        found.assignTrackingNumber(UUID.randomUUID());
        found.send();
        shipmentRepository.save(found);

        return ShipmentDTO.fromEntity(found);
    }

    public ShipmentDTO externalShipmentStatusUpdate(UpdateShipmentStatusDTO dto) {
        var found = shipmentRepository.findById(dto.orderId()).orElseThrow(() -> new IllegalStateException("Order with id " + dto.orderId() + " does not exist"));
        switch (dto.externalShipmentStatus()) {
            case SHIPPED, IN_DELIVERY,FAILED, RETURNED -> {
            }
            case DELIVERED -> {
                found.delivered();
            }
            default -> throw new IllegalStateException("Unexpected value: " + dto.externalShipmentStatus());
        }
        shipmentRepository.save(found);
        return ShipmentDTO.fromEntity(found);
    }

    public List<ShipmentDTO> findAllShipments() {
        return StreamSupport.stream(shipmentRepository.findAll().spliterator(), false)
                .map(ShipmentDTO::fromEntity)
                .toList();
    }
}
