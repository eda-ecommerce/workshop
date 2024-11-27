package com.eda.shippingService.adapters.repo;

import java.io.Serializable;
import java.util.UUID;

public class MessageCompositeKey implements Serializable {
    private UUID messageId;
    private String listenerName;
}
