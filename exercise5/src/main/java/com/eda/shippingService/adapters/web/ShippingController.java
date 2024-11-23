package com.eda.shippingService.adapters.web;

import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.domain.dto.common.AddressDTO;
import com.eda.shippingService.domain.dto.incoming.IncomingPackageDTO;
import com.eda.shippingService.domain.dto.incoming.ShipmentContentsDTO;
import com.eda.shippingService.domain.dto.incoming.UpdateShipmentStatusDTO;
import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shipment")
public class ShippingController {
    private final ShipmentService shipmentService;

    @Autowired
    public ShippingController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }
    @Operation(summary = "Get all shipments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All shipments retrieved successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShipmentDTO.class)) }),
    })
    @GetMapping("/")
    public ResponseEntity<List<ShipmentDTO>> findAllShipments() {
        return ResponseEntity.ok(shipmentService.findAllShipments());
    }

    @Operation(summary = "Request a shipment manually")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipment requested successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShipmentDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content)
    })
    @PostMapping("/{orderId}")
    public ResponseEntity<ShipmentDTO> requestShipment(@PathVariable UUID orderId, @RequestBody ShipmentContentsDTO shipmentDTO) {
        return ResponseEntity.ok(shipmentService.provideRequestedContents(orderId, shipmentDTO));
    }

    @Operation(summary = "Set shipment destination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipment destination set successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShipmentDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content)
    })
    @PostMapping("/{orderId}/destination")
    public ResponseEntity<ShipmentDTO> setDestination(@RequestBody AddressDTO addressDTO, @PathVariable UUID orderId) {
        return ResponseEntity.ok(shipmentService.provideShippingAddress(orderId, addressDTO));
    }

    @Operation(summary = "Box a shipment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipment boxed successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShipmentDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content)
    })
    @PostMapping("/{orderId}/package")
    public ResponseEntity<ShipmentDTO> boxShipment(@RequestBody IncomingPackageDTO packageDTO, @PathVariable UUID orderId) {
        var dto = shipmentService.boxShipment(orderId, packageDTO);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Update shipment status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipment status updated successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShipmentDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content)
    })
    @PostMapping("/{orderId}/status")
    public ResponseEntity<ShipmentDTO> setStatus(@RequestBody UpdateShipmentStatusDTO shipmentDTO) {
        return ResponseEntity.ok(shipmentService.externalShipmentStatusUpdate(shipmentDTO));
    }

    @Operation(summary = "Send a shipment on its way")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipment sent successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShipmentDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content)
    })
    @PutMapping("/{orderId}/send")
    public ResponseEntity<ShipmentDTO> sendShipment(@PathVariable UUID orderId) {
        return ResponseEntity.ok(shipmentService.sendShipment(orderId));
    }


}