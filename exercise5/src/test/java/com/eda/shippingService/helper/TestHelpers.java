package com.eda.shippingService.helper;

import com.eda.shippingService.domain.dto.common.AddressDTO;
import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.dto.outgoing.PackageDTO;
import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.entity.*;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestHelpers {
    public static UUID quickUUID(int value) {
        String decimalString = String.format("%012d", value);
        String uuidString = "00000000-0000-0000-0000-" + decimalString;
        return UUID.fromString(uuidString);
    }

    public static AddressDTO quickAddressDTO(String street) {
        return new AddressDTO(street, "city", "state", "postalCode", "DE");
    }

    public static Address quickAddress(String street) {
        return new Address(street, "city", "state", "postalCode", "DE");
    }

    public static class ShipmentDTOBuilder {
        UUID orderId;
        AddressDTO destination;
        PackageDTO aPackage;
        List<OrderLineItemDTO> requestedProducts = new ArrayList<>();
        ShipmentStatus status;

        /**
         * @param destinationStreet: the street of the destination address
         */
        public ShipmentDTOBuilder(UUID orderID, String destinationStreet, ShipmentStatus status) {
            this.orderId = orderID;
            this.destination = quickAddressDTO(destinationStreet);
            this.status = status;
        }

        public ShipmentDTOBuilder withPackageEmpty(UUID packageId, UUID trackingNumber) {
            this.aPackage = new PackageDTO(packageId, trackingNumber, new PackageDTO.PackageDimensions(1f, 1f, 1f, 1f), 1f, new ArrayList<>());
            return this;
        }

        public ShipmentDTOBuilder withRequestedProduct(UUID productId, int quantity) {
            this.requestedProducts.add(new OrderLineItemDTO(productId, quantity));
            return this;
        }

        public ShipmentDTOBuilder withPackageContents(UUID packageId, UUID trackingNumber, List<OrderLineItemDTO> contents) {
            this.aPackage = new PackageDTO(packageId, trackingNumber, new PackageDTO.PackageDimensions(1f, 1f, 1f, 1f), 1f, contents);
            return this;
        }

        public ShipmentDTO build() {
            return new ShipmentDTO(orderId, destination, aPackage, requestedProducts, status);
        }
    }

    public static void assertShipmentsEqual(Shipment s1, Shipment s2) {
        Assertions.assertNotNull(s1, "Shipment s1 should not be null");
        Assertions.assertNotNull(s2, "Shipment s2 should not be null");

        Assertions.assertEquals(s1.getOrderId(), s2.getOrderId(), "Order IDs should be equal");
        Assertions.assertEquals(s1.getDestination(), s2.getDestination(), "Destinations should be equal");
        assertPackagesEqual(s1.getAPackage(), s2.getAPackage());
        assertOrderLineItemsEqual(s1.getRequestedProducts(), s2.getRequestedProducts());
        Assertions.assertEquals(s1.getReserved(), s2.getReserved(), "Reserved statuses should be equal");
        Assertions.assertEquals(s1.getStatus(), s2.getStatus(), "Statuses should be equal");
    }

    private static void assertPackagesEqual(APackage p1, APackage p2) {
        if (p1 == null) {
            Assertions.assertNull(p2, "Package p2 should be null");
        }else {
            Assertions.assertNotNull(p2, "Package p2 should not be null");
            Assertions.assertEquals(p1.getTrackingNumber(), p2.getTrackingNumber(), "Tracking numbers should be equal");
            Assertions.assertEquals(p1.getDimensions(), p2.getDimensions(), "Dimensions should be equal");
            Assertions.assertEquals(p1.getWeight(), p2.getWeight(), "Weights should be equal");
            assertOrderLineItemsEqual(p1.getContents(), p2.getContents());
        }
    }

    private static void assertOrderLineItemsEqual(List<OrderLineItem> items1, List<OrderLineItem> items2) {
        Assertions.assertNotNull(items1, "OrderLineItems list items1 should not be null");
        Assertions.assertNotNull(items2, "OrderLineItems list items2 should not be null");
        Assertions.assertEquals(items1.size(), items2.size(), "OrderLineItems lists should have the same size");

        for (int i = 0; i < items1.size(); i++) {
            OrderLineItem item1 = items1.get(i);
            OrderLineItem item2 = items2.get(i);
            Assertions.assertEquals(item1.productId(), item2.productId(), "Product IDs should be equal");
            Assertions.assertEquals(item1.quantity(), item2.quantity(), "Quantities should be equal");
        }
    }
}
