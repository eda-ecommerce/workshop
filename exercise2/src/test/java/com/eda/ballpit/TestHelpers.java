package com.eda.ballpit;

import com.eda.shippingService.domain.dto.common.AddressDTO;
import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.dto.outgoing.PackageDTO;
import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.entity.Address;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestHelpers {
    public static UUID quickUUID(int value) {
        String hexString = String.format("%08x", value);
        String uuidString = "00000000-0000-0000-0000-0000" + hexString;
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
        public ShipmentDTOBuilder(UUID orderID, String destinationStreet, ShipmentStatus status){
            this.orderId = orderID;
            this.destination = quickAddressDTO(destinationStreet);
            this.status = status;
        }

        public ShipmentDTOBuilder withPackageEmpty(UUID packageId, UUID trackingNumber){
            this.aPackage = new PackageDTO(packageId, trackingNumber, new PackageDTO.PackageDimensions(1f,1f,1f,1f),1f, new ArrayList<>());
            return this;
        }

        public ShipmentDTOBuilder withRequestedProduct(UUID productId, int quantity){
            this.requestedProducts.add(new OrderLineItemDTO(productId, quantity));
            return this;
        }

        public ShipmentDTOBuilder withPackageContents(UUID packageId, UUID trackingNumber, List<OrderLineItemDTO> contents){
            this.aPackage = new PackageDTO(packageId, trackingNumber, new PackageDTO.PackageDimensions(1f,1f,1f,1f),1f, contents);
            return this;
        }

        public ShipmentDTO build(){
            return new ShipmentDTO(orderId, destination, aPackage, requestedProducts, status);
        }
    }

    @ParameterizedTest(name = "Test getUUID with {0}")
    @ValueSource(ints = {1, 22, 333, 4444, 2147483647})
    public void testGetUUID(Integer parameter) {
        UUID uuid = quickUUID(parameter);
        Assertions.assertEquals(String.format("00000000-0000-0000-0000-0000%08x", parameter), uuid.toString());
    }

    @Test
    public void shouldBuildValidShipmentDTO(){
        ShipmentDTO expected =
                new ShipmentDTO(
                        quickUUID(1),
                        new AddressDTO("street", "city", "state", "postalCode", "DE"),
                        new PackageDTO(quickUUID(999), quickUUID(3), new PackageDTO.PackageDimensions(1f,1f,1f,1f),1f, List.of(new OrderLineItemDTO(quickUUID(3), 1))),
                        List.of(new OrderLineItemDTO(quickUUID(3), 1)),
                        ShipmentStatus.CONFIRMED
                );
        ShipmentDTO actual = new ShipmentDTOBuilder(quickUUID(1), "street", ShipmentStatus.CONFIRMED)
                .withRequestedProduct(quickUUID(3), 1)
                .withPackageContents(quickUUID(999), quickUUID(3), List.of(new OrderLineItemDTO(quickUUID(3), 1)))
                .build();
        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(actual.toEntity().checkContents());
    }
}
