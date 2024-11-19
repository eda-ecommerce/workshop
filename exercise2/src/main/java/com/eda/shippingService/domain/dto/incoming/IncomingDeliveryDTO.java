package com.eda.shippingService.domain.dto.incoming;

import com.eda.shippingService.domain.entity.OrderLineItem;

import java.util.List;
import java.util.UUID;

//very similar to PackageDTO, but we dont need the trackingNumber as this is just a DTO for the incoming delivery
public record IncomingDeliveryDTO(
        UUID id,
        //consider this to be an 'order' placed by our company, and not the external user
        List<OrderLineItem> contents
) {}
