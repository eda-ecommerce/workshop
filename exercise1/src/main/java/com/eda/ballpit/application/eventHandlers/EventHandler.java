package com.eda.ballpit.application.eventHandlers;

public interface EventHandler<T> {
    void handle(T event);
}
