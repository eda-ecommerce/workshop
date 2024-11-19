package com.eda.shippingService.application.eventHandlers;

public interface EventHandler<T> {
    void handle(T event);
}
