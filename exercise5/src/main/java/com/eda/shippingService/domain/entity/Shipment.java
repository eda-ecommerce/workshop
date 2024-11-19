package com.eda.shippingService.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Shipment{
    //We use the orderId as the primary key, as it is unique and lets us match it to order way quicker
    @Getter
    @Id
    private UUID orderId;

    @Embedded
    private Address destination;

    //At the moment we assume one package per shipment, but this could easily be changed to a list of packages
    @OneToOne(cascade = CascadeType.DETACH)
    private APackage aPackage;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<OrderLineItem> requestedProducts;
    private Boolean reserved = false;

    @Transient
    private HashMap<UUID, Integer> requestedHashMap;

    private ShipmentStatus status;

    public Shipment(UUID orderId, Address destination, APackage aPackage, List<OrderLineItem> orderLineItems, ShipmentStatus status){
        this.orderId = orderId;
        this.destination = destination;
        this.aPackage = aPackage;
        this.requestedProducts = orderLineItems != null ?orderLineItems: List.of();
        this.status = status;
    }

    public HashMap<UUID, Integer> getRequestedProductsAsHashMap(){
        if (requestedHashMap == null){
            requestedHashMap = new HashMap<>();
            for (OrderLineItem orderLineItem : this.getRequestedProducts()){
                requestedHashMap.put(orderLineItem.productId(), orderLineItem.quantity());
            }
        }
        return requestedHashMap;
    }

    public Integer getProductQuantity(UUID productId){
        return requestedHashMap.getOrDefault(productId, 0);
    }

    /**
     * Adds a package and sets the status to packaged,
     * if the shipment is confirmed and the contents are the requested products
     * @throws IllegalStateException if the shipment is not confirmed
     * @throws IllegalArgumentException if the package contains products not requested or the quantities are incorrect
     * @param aPackage the package to add
     */
    public void addPackage(APackage aPackage){
        if (this.status == ShipmentStatus.CONFIRMED){
            aPackage.validateContents(this.requestedProducts);
            this.aPackage = aPackage;
            this.status = ShipmentStatus.PACKAGED;
        }
        else {
            throw new IllegalStateException("Shipment must be confirmed before it can be packed");
        }
    }

    public void assignTrackingNumber(UUID trackingNumber){
        this.aPackage.assignTrackingNumber(trackingNumber);
    }

    public boolean validateAddresses()
    {
        return destination.validate();
    }

    /**
     * Checks if the contents of the package match the requested products
     * @return true if the contents match the requested products
     */
    public boolean checkContents(){
        return aPackage.validateContents(requestedProducts);
    }

    public void approve(){
        if (this.status == ShipmentStatus.INCOMPLETE && this.reserved && validateAddresses()){
            this.status = ShipmentStatus.CONFIRMED;
        } else {
            throw new IllegalStateException("Shipment is in state: "+this.status+" and cannot be approved.");
        }
    }

    public void send(){
        if (this.status != ShipmentStatus.PACKAGED){
            throw new IllegalStateException("Shipment must be packaged before it can be sent");
        }
        this.status = ShipmentStatus.SHIPPED;
    }

    public void delivered(){
        if (this.status != ShipmentStatus.SHIPPED){
            throw new IllegalStateException("Shipment must be shipped before it can be delivered");
        }
        this.status = ShipmentStatus.DELIVERED;
    }

    public void reserve(){
        if (this.status != ShipmentStatus.INCOMPLETE || this.reserved){
            throw new IllegalStateException("Shipment cannot be reserved again. Current status: " + this.status);
        }
        this.reserved = true;
    }

}
