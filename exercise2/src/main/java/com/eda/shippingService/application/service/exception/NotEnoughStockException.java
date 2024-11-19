package com.eda.shippingService.application.service.exception;

public class NotEnoughStockException extends Exception {
    public NotEnoughStockException(String message) {
        super(message);
    }
}
