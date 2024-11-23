package com.eda.shippingService;

import com.eda.shippingService.domain.dto.common.AddressDTO;
import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.dto.outgoing.PackageDTO;
import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.eda.shippingService.TestHelpers.quickUUID;

public class TestHelperTests {

    @Test
    public void testGetUUID() {
        UUID uuid = quickUUID(2147483647);
        UUID uuid2 = quickUUID(1);
        Assertions.assertEquals("00000000-0000-0000-0000-002147483647", uuid.toString());
        Assertions.assertEquals("00000000-0000-0000-0000-000000000001", uuid2.toString());

    }

    @Test
    public void shouldBuildValidShipmentDTO() {
        ShipmentDTO expected =
                new ShipmentDTO(
                        quickUUID(1),
                        new AddressDTO("street", "city", "state", "postalCode", "DE"),
                        new PackageDTO(quickUUID(999), quickUUID(3), new PackageDTO.PackageDimensions(1f, 1f, 1f, 1f), 1f, List.of(new OrderLineItemDTO(quickUUID(3), 1))),
                        List.of(new OrderLineItemDTO(quickUUID(3), 1)),
                        ShipmentStatus.CONFIRMED
                );
        ShipmentDTO actual = new TestHelpers.ShipmentDTOBuilder(quickUUID(1), "street", ShipmentStatus.CONFIRMED)
                .withRequestedProduct(quickUUID(3), 1)
                .withPackageContents(quickUUID(999), quickUUID(3), List.of(new OrderLineItemDTO(quickUUID(3), 1)))
                .build();
        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(actual.toEntity().checkContents());
    }
}
