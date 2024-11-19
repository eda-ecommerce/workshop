package com.eda.shippingService.adapters.web;

import com.eda.shippingService.application.service.StockService;
import com.eda.shippingService.domain.dto.incoming.IncomingDeliveryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Stock Controller", description = "APIs for managing stock")
public class StockController {
    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/incomingDelivery")
    @Operation(summary = "Process incoming delivery")
    public void processIncomingDelivery(@RequestBody IncomingDeliveryDTO incomingDeliveryDTO) {

    }
}
