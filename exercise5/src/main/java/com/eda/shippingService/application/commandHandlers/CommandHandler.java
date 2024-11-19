package com.eda.shippingService.application.commandHandlers;

public interface CommandHandler<T>{
    void handle(T command);
}
