package com.eda.shippingService.ui;


import com.eda.shippingService.adapters.eventing.EventPublisher;
import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.application.service.StockService;
import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.dto.incoming.ShipmentContentsDTO;
import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.entity.Product;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Route
public class MainView extends VerticalLayout {
    //Just a PoC
    private final ShipmentService shipmentService;
    private final StockService stockService;
    private final EventPublisher eventPublisher;

    private final Product p1 = new Product(quickUUID(123), 10);
    private final Product p2 = new Product(quickUUID(1234), 10);
    private final Product p3 = new Product(quickUUID(12345), 10);
    private final List<Product> products = List.of(p1, p2, p3);

    @Autowired
    public MainView(
            ShipmentService shipmentService, StockService stockService, EventPublisher eventPublisher
    ) {
        this.shipmentService = shipmentService;
        this.stockService = stockService;
        this.eventPublisher = eventPublisher;
        add(new Button("Click me", e ->
                Notification.show("Shipment created with orderID: " + createTestShipment().orderId())));
        createProducts();
        addProductSelection();
    }

    void addProductSelection(){
        Select<Product> productSelect = new Select<>();
        productSelect.setLabel("Select a product");
        productSelect.setItems(List.of(p1, p2, p3));
        productSelect.setItemLabelGenerator(Product::getStorageLocation);
        createProducts();
        add(productSelect);
    }

    public void createProducts(){
        for (Product product : products) {
            stockService.registerNewProduct(product.getId(), product.getAvailableStock());
        }
    }

    public ShipmentDTO createTestShipment(){
        return shipmentService.provideRequestedContents(UUID.randomUUID(),
                new ShipmentContentsDTO(UUID.randomUUID(),
                        List.of(new OrderLineItemDTO(UUID.randomUUID(), 1))));
    }

    static UUID quickUUID(int value) {
        String hexString = String.format("%08x", value);
        String uuidString = "00000000-0000-0000-0000-0000" + hexString;
        return UUID.fromString(uuidString);
    }
}
