package com.eda.shippingService;

import com.eda.shippingService.adapters.repo.ProductRepository;
import com.eda.shippingService.application.service.StockService;
import com.eda.shippingService.application.service.exception.NotEnoughStockException;
import com.eda.shippingService.domain.entity.OrderLineItem;
import com.eda.shippingService.domain.entity.Product;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.eda.shippingService.TestHelpers.quickUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;

@SpringBootTest(classes = StockService.class)
public class StockServiceTest {

    @MockBean
    private StockService stockService;

    @MockBean
    private ProductRepository productRepository;

    @Test
    public void shouldRegisterNewProduct(){
        // Given
        Product testProduct = new Product(
                quickUUID(1),
                4
        );

        // Mock
        // using anyInt() instead of any() to avoid NullPointerException
        Mockito.doNothing().when(stockService).registerNewProduct(Mockito.any(), anyInt());

        // When
        stockService.registerNewProduct(testProduct.getId(), testProduct.getPhysicalStock());

        // Then
        Mockito.verify(stockService, Mockito.times(1)).registerNewProduct(quickUUID(1), 4);
        ArgumentCaptor<UUID> productUUIDArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Integer> productStockArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(stockService).registerNewProduct(productUUIDArgumentCaptor.capture(), productStockArgumentCaptor.capture());
        UUID capturedProductUUID = productUUIDArgumentCaptor.getValue();
        Integer capturedProductStock = productStockArgumentCaptor.getValue();
        assertEquals(testProduct.getId(), capturedProductUUID);
        assertEquals(testProduct.getPhysicalStock(), capturedProductStock);
    }

    @Test
    public void shouldReserveStock() throws NotEnoughStockException {
        // Given
        int reserveStockBy = 2;
        int physicalStock = 5;
        UUID testProductId = quickUUID(1);
        Product testProduct = new Product(
                testProductId,
                physicalStock
        );

        // Mocks
        Mockito.when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        Mockito.doAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            int amount = invocation.getArgument(1);

            assertEquals(testProductId, id);
            assertEquals(reserveStockBy, amount);

            Product product = productRepository.findById(id).orElseThrow();
            product.reserveStock(amount);
            return null;
        }).when(stockService).reserveStock(Mockito.any(), anyInt());

        // When
        stockService.reserveStock(testProductId, reserveStockBy);

        // Then
        Mockito.verify(stockService, Mockito.times(1)).reserveStock(testProductId, reserveStockBy);
        ArgumentCaptor<UUID> productUUIDArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Integer> productStockArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(stockService).reserveStock(productUUIDArgumentCaptor.capture(), productStockArgumentCaptor.capture());
        UUID capturedProductUUID = productUUIDArgumentCaptor.getValue();
        Integer capturedProductStock = productStockArgumentCaptor.getValue();
        assertEquals(testProductId, capturedProductUUID);
        assertEquals(reserveStockBy, capturedProductStock);
        assertEquals(physicalStock - reserveStockBy, testProduct.getAvailableStock());
    }

    @Test
    public void shouldReleaseStock(){
        // Given
        int releaseStockBy = 1;
        int physicalStock = 6;
        int reservedStock = 3;
        UUID testProductId = quickUUID(1);
        Product testProduct = new Product(
                testProductId,
                physicalStock
        );
        testProduct.setReservedStock(reservedStock);

        // Mocks
        Mockito.when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        Mockito.doAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            int amount = invocation.getArgument(1);

            assertEquals(testProductId, id);
            assertEquals(releaseStockBy, amount);

            Product product = productRepository.findById(id).orElseThrow();
            product.releaseStock(amount);
            return null;
        }).when(stockService).releaseStock(Mockito.any(), anyInt());

        // When
        stockService.releaseStock(testProductId, releaseStockBy);

        // Then
        Mockito.verify(stockService, Mockito.times(1)).releaseStock(testProductId, releaseStockBy);
        ArgumentCaptor<UUID> productUUIDArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Integer> productReleaseStockArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(stockService).releaseStock(productUUIDArgumentCaptor.capture(), productReleaseStockArgumentCaptor.capture());
        UUID capturedProductUUID = productUUIDArgumentCaptor.getValue();
        Integer capturedReleaseStock = productReleaseStockArgumentCaptor.getValue();
        assertEquals(testProductId, capturedProductUUID);
        assertEquals(releaseStockBy, capturedReleaseStock);
        assertEquals(physicalStock - (reservedStock - releaseStockBy), testProduct.getAvailableStock());
        assertEquals(reservedStock - releaseStockBy, testProduct.getReservedStock());
    }

    @Test
    public void shouldIncreaseStock() {
        // Given
        int increaseStockBy = 3;
        UUID testProductId = quickUUID(1);
        Product testProduct = new Product(
                testProductId,
                7
        );

        // Mocks
        Mockito.when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        Mockito.doAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            int amount = invocation.getArgument(1);

            assertEquals(testProductId, id);
            assertEquals(increaseStockBy, amount);

            Product product = productRepository.findById(id).orElseThrow();
//            product.setPhysicalStock(product.getPhysicalStock() + amount);
            product.increaseStock(amount);
            return null;
        }).when(stockService).increaseStock(Mockito.any(), anyInt());

        // When
        stockService.increaseStock(testProductId, increaseStockBy);

        // Then
        Mockito.verify(stockService, Mockito.times(1)).increaseStock(testProductId, increaseStockBy);
        ArgumentCaptor<UUID> productUUIDArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Integer> productStockArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(stockService).increaseStock(productUUIDArgumentCaptor.capture(), productStockArgumentCaptor.capture());
        UUID capturedProductUUID = productUUIDArgumentCaptor.getValue();
        Integer capturedProductStock = productStockArgumentCaptor.getValue();
        assertEquals(testProductId, capturedProductUUID);
        assertEquals(increaseStockBy, capturedProductStock);
        assertEquals(10, testProduct.getPhysicalStock());
    }

    @Test
    public void shouldDecreaseStock(){
        // Given
        int decreaseStockBy = 2;
        UUID testProductId = quickUUID(1);
        Product testProduct = new Product(
                testProductId,
                10
        );

        // Mocks
        Mockito.when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        Mockito.doAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            int amount = invocation.getArgument(1);

            assertEquals(testProductId, id);
            assertEquals(decreaseStockBy, amount);

            Product product = productRepository.findById(id).orElseThrow();
            product.decreaseStock(amount);
            return null;
        }).when(stockService).decreaseStock(Mockito.any(), anyInt());

        // When
        stockService.decreaseStock(testProductId, decreaseStockBy);

        // Then
        Mockito.verify(stockService, Mockito.times(1)).decreaseStock(testProductId, decreaseStockBy);
        ArgumentCaptor<UUID> productUUIDArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Integer> productStockArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(stockService).decreaseStock(productUUIDArgumentCaptor.capture(), productStockArgumentCaptor.capture());
        UUID capturedProductUUID = productUUIDArgumentCaptor.getValue();
        Integer capturedProductStock = productStockArgumentCaptor.getValue();
        assertEquals(testProductId, capturedProductUUID);
        assertEquals(decreaseStockBy, capturedProductStock);
        assertEquals(8, testProduct.getPhysicalStock());
    }

    @Test
    public void shouldDecreaseAndReleaseStock(){
        // Given
        int decreaseStockBy = 2;
        int physicalStock = 23;
        int reservedStock = 5;
        UUID testProductId = quickUUID(1);
        Product testProduct = new Product(
                testProductId,
                physicalStock
        );
        testProduct.setReservedStock(reservedStock);

        // Mocks
        Mockito.when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        Mockito.doAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            int amount = invocation.getArgument(1);

            assertEquals(testProductId, id);
            assertEquals(decreaseStockBy, amount);

            Product product = productRepository.findById(id).orElseThrow();
            product.decreaseStock(amount);
            product.releaseStock(amount);
            return null;
        }).when(stockService).decreaseAndReleaseStock(Mockito.any(), anyInt());

        // When
        stockService.decreaseAndReleaseStock(testProductId, decreaseStockBy);

        // Then
        Mockito.verify(stockService, Mockito.times(1)).decreaseAndReleaseStock(testProductId, decreaseStockBy);
        ArgumentCaptor<UUID> productUUIDArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Integer> productStockArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(stockService).decreaseAndReleaseStock(productUUIDArgumentCaptor.capture(), productStockArgumentCaptor.capture());
        UUID capturedProductUUID = productUUIDArgumentCaptor.getValue();
        Integer capturedProductStock = productStockArgumentCaptor.getValue();
        assertEquals(testProductId, capturedProductUUID);
        assertEquals(decreaseStockBy, capturedProductStock);
        assertEquals(physicalStock - decreaseStockBy, testProduct.getPhysicalStock());
        assertEquals(reservedStock - decreaseStockBy, testProduct.getReservedStock());
        assertEquals(physicalStock - reservedStock, testProduct.getAvailableStock());
    }

    @Test
    public void shouldSetStock() {
        // Given
        UUID testProductId = quickUUID(1);
        int finalPhysicalStock = 15;
        int finalReservedStock = 3;
        Product testProduct = new Product(
                testProductId,
                8
        );
        testProduct.setReservedStock(4);

        // Mocks
        Mockito.when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        Mockito.doAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            int newPhysicalStock = invocation.getArgument(1);
            int newReservedStock = invocation.getArgument(2);

            assertEquals(testProductId, id);
            assertEquals(finalPhysicalStock, newPhysicalStock);
            assertEquals(finalReservedStock, newReservedStock);

            Product product = productRepository.findById(id).orElseThrow();
            product.setPhysicalStock(newPhysicalStock);
            product.setReservedStock(newReservedStock);
            return null;
        }).when(stockService).setStock(Mockito.any(), anyInt(), anyInt());

        // When
        stockService.setStock(testProductId, finalPhysicalStock, finalReservedStock);

        // Then
        Mockito.verify(stockService, Mockito.times(1)).setStock(testProductId, finalPhysicalStock, finalReservedStock);
        ArgumentCaptor<UUID> productUUIDArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Integer> physicalStockArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> reservedStockArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(stockService).setStock(productUUIDArgumentCaptor.capture(), physicalStockArgumentCaptor.capture(), reservedStockArgumentCaptor.capture());
        UUID capturedProductUUID = productUUIDArgumentCaptor.getValue();
        Integer capturedPhysicalStock = physicalStockArgumentCaptor.getValue();
        Integer capturedReservedStock = reservedStockArgumentCaptor.getValue();
        assertEquals(testProductId, capturedProductUUID);
        assertEquals(finalPhysicalStock, capturedPhysicalStock);
        assertEquals(finalReservedStock, capturedReservedStock);
        assertEquals(finalPhysicalStock - finalReservedStock, testProduct.getAvailableStock());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldBatchAdjustStockWithAHashMapOfProducts(){
        // Given
        UUID testProductId1 = quickUUID(1);
        UUID testProductId2 = quickUUID(2);
        UUID testProductId3 = quickUUID(3);
        int testProduct1Stock = 5;
        int testProduct2Stock = 10;
        int testProduct3Stock = 15;
        int increaseProduct1StockBy = 12;
        int increaseProduct2StockBy = 32;
        int increaseProduct3StockBy = 45;
        Product testProduct1 = new Product(
                testProductId1,
                testProduct1Stock
        );
        Product testProduct2 = new Product(
                testProductId2,
                testProduct2Stock
        );
        Product testProduct3 = new Product(
                testProductId3,
                testProduct3Stock
        );

        // Mocks
        Mockito.when(productRepository.findById(testProductId1)).thenReturn(Optional.of(testProduct1));
        Mockito.when(productRepository.findById(testProductId2)).thenReturn(Optional.of(testProduct2));
        Mockito.when(productRepository.findById(testProductId3)).thenReturn(Optional.of(testProduct3));
        //TODO: this is not invoked
        Mockito.doAnswer(invocation -> {
            Map<UUID, Integer> testMap = invocation.getArgument(0);

            // TODO: not really satisfied with these assertions
            for(UUID id: testMap.keySet()){
                System.out.println(id);
                if (id == testProductId1)
                    assertEquals(testMap.get(id), increaseProduct1StockBy);
                else if (id == testProductId2)
                    assertEquals(testMap.get(id), increaseProduct2StockBy);
                else if (id == testProductId3)
                    assertEquals(testMap.get(id), increaseProduct3StockBy);
            }

            for(UUID id : testMap.keySet()){
                Product product = productRepository.findById(id).orElseThrow();
                // print out the id on the console
                System.out.println(id);
                product.increaseStock(testMap.get(id));
            }

            return null;
        }).when(stockService).batchAdjustStock(Map.of());

        // When
        stockService.batchAdjustStock(Map.of(
                testProductId1, increaseProduct1StockBy,
                testProductId2, increaseProduct2StockBy,
                testProductId3, increaseProduct3StockBy
        ));

        // Then
        Mockito.verify(stockService, Mockito.times(1)).batchAdjustStock(Map.of(testProductId1, increaseProduct1StockBy, testProductId2, increaseProduct2StockBy, testProductId3, increaseProduct3StockBy));
        ArgumentCaptor<Map<UUID, Integer>> productMapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(stockService).batchAdjustStock(productMapArgumentCaptor.capture());
        Map<UUID, Integer> capturedProductMap = productMapArgumentCaptor.getValue();
        for (UUID id : capturedProductMap.keySet()){
            if (id == testProductId1) {
                assertEquals(testProductId1, id);
                assertEquals(increaseProduct1StockBy, capturedProductMap.get(id));
                assertEquals(testProduct1Stock + increaseProduct1StockBy, testProduct1.getPhysicalStock());
            }
            else if (id == testProductId2) {
                assertEquals(testProductId2, id);
                assertEquals(increaseProduct2StockBy, capturedProductMap.get(id));
                assertEquals(testProduct2Stock + increaseProduct2StockBy, testProduct2.getPhysicalStock());
            }
            else if (id == testProductId3) {
                assertEquals(testProductId3, id);
                assertEquals(increaseProduct3StockBy, capturedProductMap.get(id));
                assertEquals(testProduct3Stock + increaseProduct3StockBy, testProduct3.getPhysicalStock());
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldBatchAdjustStockWithAListOfOrderLineItems() {
        // Given
        UUID testProductId1 = quickUUID(1);
        UUID testProductId2 = quickUUID(2);
        UUID testProductId3 = quickUUID(3);
        int testProduct1Stock = 5;
        int testProduct2Stock = 10;
        int testProduct3Stock = 15;
        int increaseProduct1StockBy = 12;
        int increaseProduct2StockBy = 32;
        int increaseProduct3StockBy = 45;
        Product testProduct1 = new Product(
                testProductId1,
                testProduct1Stock
        );
        Product testProduct2 = new Product(
                testProductId2,
                testProduct2Stock
        );
        Product testProduct3 = new Product(
                testProductId3,
                testProduct3Stock
        );

        // Mocks
        Mockito.when(productRepository.findById(testProductId1)).thenReturn(Optional.of(testProduct1));
        Mockito.when(productRepository.findById(testProductId2)).thenReturn(Optional.of(testProduct2));
        Mockito.when(productRepository.findById(testProductId3)).thenReturn(Optional.of(testProduct3));
        // TODO: this is not invoked
        Mockito.doAnswer(invocation -> {
            List<OrderLineItem> testOrderLineItem = invocation.getArgument(0);

            for (OrderLineItem item : testOrderLineItem) {
                if (item.productId() == testProductId1)
                    assertEquals(item.quantity(), increaseProduct1StockBy);
                else if (item.productId() == testProductId2)
                    assertEquals(item.quantity(), increaseProduct2StockBy);
                else if (item.productId() == testProductId3)
                    assertEquals(item.quantity(), increaseProduct3StockBy);
            }

            for (OrderLineItem item : testOrderLineItem) {
                Product product = productRepository.findById(item.productId()).orElseThrow();
                product.increaseStock(item.quantity());
            }
            return null;
        }).when(stockService).batchAdjustStock(List.of());

        // When
        stockService.batchAdjustStock(List.of(
                new OrderLineItem(testProductId1, increaseProduct1StockBy),
                new OrderLineItem(testProductId2, increaseProduct2StockBy),
                new OrderLineItem(testProductId3, increaseProduct3StockBy)
        ));

        // Then
        Mockito.verify(stockService, Mockito.times(1)).batchAdjustStock(List.of(new OrderLineItem(testProductId1, increaseProduct1StockBy), new OrderLineItem(testProductId2, increaseProduct2StockBy), new OrderLineItem(testProductId3, increaseProduct3StockBy)));
        ArgumentCaptor<List<OrderLineItem>> orderLineItemArgumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(stockService).batchAdjustStock(orderLineItemArgumentCaptor.capture());
        List<OrderLineItem> capturedOrderLineItems = orderLineItemArgumentCaptor.getValue();
        System.out.println(capturedOrderLineItems);
        for (OrderLineItem item : capturedOrderLineItems) {
            if (item.productId() == testProductId1) {
                assertEquals(testProductId1, item.productId());
                assertEquals(increaseProduct1StockBy, item.quantity());
                assertEquals(testProduct1Stock + increaseProduct1StockBy, testProduct1.getPhysicalStock());
            } else if (item.productId() == testProductId2) {
                assertEquals(testProductId2, item.productId());
                assertEquals(increaseProduct2StockBy, item.quantity());
                assertEquals(testProduct2Stock + increaseProduct2StockBy, testProduct2.getPhysicalStock());
            } else if (item.productId() == testProductId3) {
                assertEquals(testProductId3, item.productId());
                assertEquals(increaseProduct3StockBy, item.quantity());
                assertEquals(testProduct3Stock + increaseProduct3StockBy, testProduct3.getPhysicalStock());
            }
        }
    }
}
