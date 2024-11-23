package com.eda.shippingService;

import com.eda.shippingService.adapters.repo.ShipmentRepository;
import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.application.service.exception.IncompleteContentException;
import com.eda.shippingService.domain.dto.common.AddressDTO;
import com.eda.shippingService.domain.dto.common.OrderLineItemDTO;
import com.eda.shippingService.domain.dto.common.PackageDimensionsDTO;
import com.eda.shippingService.domain.dto.incoming.IncomingPackageDTO;
import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.entity.OrderLineItem;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.UUID;

import static com.eda.shippingService.TestHelpers.quickAddress;
import static com.eda.shippingService.TestHelpers.quickUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ShipmentService.class)
public class ShipmentServiceTest {

    @MockBean
    private ShipmentService shipmentService;

    @MockBean
    private ShipmentRepository shipmentRepository;

    @Test
    public void shouldProvideShippingAddress() {
        // Given
        Shipment shipment = new Shipment(
                quickUUID(1),
                quickAddress("Street1"),
                null,
                List.of(
                        new OrderLineItem(quickUUID(2), 1)
                ),
                ShipmentStatus.CONFIRMED
        );

        // Mocks
        Mockito.when(shipmentRepository.findByOrderId(Mockito.any())).thenReturn(shipment);
        Mockito.when(shipmentRepository.save(Mockito.any())).thenReturn(shipment);
        Mockito.when(shipmentService.provideShippingAddress(Mockito.any(), Mockito.any())).thenReturn(Mockito.mock(ShipmentDTO.class));

        // When
        shipmentService.provideShippingAddress(quickUUID(1), AddressDTO.fromEntity(quickAddress("Street2")));

        // Then
        Mockito.verify(shipmentService, Mockito.times(1)).provideShippingAddress(quickUUID(1), AddressDTO.fromEntity(quickAddress("Street2")));
        ArgumentCaptor<UUID> shipmentUUID = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<AddressDTO> addressDTO = ArgumentCaptor.forClass(AddressDTO.class);
        Mockito.verify(shipmentService).provideShippingAddress(shipmentUUID.capture(), addressDTO.capture());
        assertEquals(quickUUID(1), shipmentUUID.getValue());
        assertEquals(AddressDTO.fromEntity(quickAddress("Street2")), addressDTO.getValue());
    }

    @Test
    public void shouldApproveShipment() {
        // Given
        Shipment shipment = new Shipment(
                quickUUID(1),
                quickAddress("Street1"),
                null,
                List.of(
                        new OrderLineItem(quickUUID(2), 1)
                ),
                ShipmentStatus.INCOMPLETE
        );
        shipment.reserve();

        // Mocks
        Mockito.when(shipmentRepository.findByOrderId(Mockito.any())).thenReturn(shipment);
        Mockito.doAnswer(
                invocation -> {
                    UUID id = invocation.getArgument(0);

//                    assertEquals(ShipmentStatus.CONFIRMED, testShipment.getStatus());

                    Shipment shipment1 = shipmentRepository.findByOrderId(id);
                    if (shipment1 != null) shipment1.approve();
                    return null;
                }
        ).when(shipmentService).approveShipment(Mockito.any());

        // Given
        shipmentService.approveShipment(quickUUID(1));

        // Then
        Mockito.verify(shipmentService, Mockito.times(1)).approveShipment(quickUUID(1));
        ArgumentCaptor<UUID> shipmentUUID = ArgumentCaptor.forClass(UUID.class);
        Mockito.verify(shipmentService).approveShipment(shipmentUUID.capture());
        assertEquals(quickUUID(1), shipmentUUID.getValue());
        assertEquals(ShipmentStatus.CONFIRMED, shipment.getStatus());
    }

    @Test
    public void shouBoxShipment() throws IncompleteContentException {
        // Given
        Shipment shipment = new Shipment(
                quickUUID(1),
                quickAddress("Street1"),
                null,
                List.of(
                        new OrderLineItem(quickUUID(2), 1),
                        new OrderLineItem(quickUUID(3), 1)
                ),
                ShipmentStatus.CONFIRMED
        );
        IncomingPackageDTO incomingPackage = new IncomingPackageDTO(
                new PackageDimensionsDTO(1F, 1F, 1F),
                3F,
                List.of(
                        new OrderLineItemDTO(quickUUID(2), 1)
                )
        );

        // Mocks
        Mockito.when(shipmentRepository.findByOrderId(Mockito.any())).thenReturn(shipment);
        Mockito.doAnswer(
                invocation -> {
                    UUID id = invocation.getArgument(0);
                    IncomingPackageDTO incomingPackageDTO = invocation.getArgument(1);

                    assertEquals(id, shipment.getOrderId());

                    Shipment shipment1 = shipmentRepository.findByOrderId(id);
                    if (shipment1 != null) shipment1.addPackage(incomingPackageDTO.toPackage());
                    return null;
                }
        ).when(shipmentService).boxShipment(Mockito.any(), Mockito.any());

        // When
        shipmentService.boxShipment(quickUUID(1), incomingPackage);

        // Then
        Mockito.verify(shipmentService, Mockito.times(1)).boxShipment(quickUUID(1), incomingPackage);
        ArgumentCaptor<UUID> shipmentUUID = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<IncomingPackageDTO> incomingPackageDTO = ArgumentCaptor.forClass(IncomingPackageDTO.class);
        Mockito.verify(shipmentService).boxShipment(shipmentUUID.capture(), incomingPackageDTO.capture());
        assertEquals(quickUUID(1), shipmentUUID.getValue());
        assertEquals(incomingPackage, incomingPackageDTO.getValue());
        assertEquals(ShipmentStatus.PACKAGED, shipment.getStatus());
    }
}