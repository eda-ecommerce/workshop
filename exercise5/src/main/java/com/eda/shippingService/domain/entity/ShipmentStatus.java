package com.eda.shippingService.domain.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum ShipmentStatus {
    INCOMPLETE, //We still need some data or the order is not yet confirmed
    CONFIRMED, //The order has been confirmed
    PACKAGED, //The worker pushed a button on his terminal packing the required contents
    SHIPPED, //The package has been handed over to the delivery service
    DELIVERED, // C'est arrivé!
    RETURNED, //The package was returned to the sender
    ON_HOLD; //Something went wrong!
}
