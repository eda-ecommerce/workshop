package com.eda.shippingService.domain.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class APackage extends AbstractEntity{
    @Nullable
    private UUID trackingNumber;
    @Embedded
    private PackageDimensions dimensions;
    @Setter
    private Float weight;
    @ElementCollection
    private List<OrderLineItem> contents = List.of();

    public APackage(Float height, Float width, Float depth, Float weight, List<OrderLineItem> contents){
        this.weight = weight;
        this.contents = contents;
        this.dimensions = new PackageDimensions(height, width, depth, height * width * depth);
    }

    public void assignTrackingNumber(UUID trackingNumber){
        this.trackingNumber = trackingNumber;
    }

    public Boolean validateContents(List<OrderLineItem> requested){
        var requestedHashMap = new HashMap<UUID, Integer>();
        for (OrderLineItem orderLineItem : requested){
            requestedHashMap.put(orderLineItem.productId(), orderLineItem.quantity());
        }
        for (OrderLineItem orderLineItem : contents){
            if (requestedHashMap.get(orderLineItem.productId()) == null){
                throw new IllegalArgumentException("Package contains product not requested");
            }
            if (!requestedHashMap.get(orderLineItem.productId()).equals(orderLineItem.quantity())){
                throw new IllegalArgumentException("Package contains more or less of a product than requested");
            }
        }
        return true;
    }
}
