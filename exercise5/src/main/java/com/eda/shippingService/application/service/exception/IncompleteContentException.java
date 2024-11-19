package com.eda.shippingService.application.service.exception;

public class IncompleteContentException extends RuntimeException {
    public IncompleteContentException(String msg){
        super(msg);
    }
}
