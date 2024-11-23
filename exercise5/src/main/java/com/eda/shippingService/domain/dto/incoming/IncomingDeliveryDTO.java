package com.eda.shippingService.domain.dto.incoming;

import com.eda.shippingService.domain.entity.OrderLineItem;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.UUID;

//very similar to PackageDTO, but we dont need the trackingNumber as this is just a DTO for the incoming delivery
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record IncomingDeliveryDTO(
        UUID id,
        //consider this to be an 'order' placed by our company, and not the external user
        List<OrderLineItem> contents
) {}
