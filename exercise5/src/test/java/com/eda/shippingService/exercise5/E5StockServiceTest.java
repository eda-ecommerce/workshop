package com.eda.shippingService.exercise5;

import com.eda.shippingService.adapters.eventing.KafkaEventPublisher;
import com.eda.shippingService.adapters.repo.ProductRepository;
import com.eda.shippingService.application.service.StockServiceImpl;
import com.eda.shippingService.application.service.exception.NotEnoughStockException;
import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.events.AvailableStockAdjusted;
import com.eda.shippingService.domain.events.OutOfStock;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static com.eda.shippingService.helper.TestHelpers.quickUUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = {StockServiceImpl.class})
public class E5StockServiceTest {
    @MockBean
    private KafkaEventPublisher eventPublisher;
    @MockBean
    private ProductRepository productRepository;
    @Autowired
    private StockServiceImpl stockService;

    @Value("${kafka.topic.stock}")
    private String stockTopic;

    @Test
    void shouldCallEventPublisherWithCorrectEvent() throws NotEnoughStockException {
        Mockito.doNothing().when(eventPublisher).publish(any(), any());
        var expProduct =  new Product(quickUUID(123), 100);
        Mockito.when(productRepository.findById(any())).thenReturn(Optional.of(expProduct));
        expProduct.reserveStock(5);
        Mockito.when(productRepository.save(any(Product.class))).thenReturn(
               expProduct
        );
        //when
        stockService.reserveStock(quickUUID(123), 5);
        //then
        Mockito.verify(eventPublisher).publish(any(AvailableStockAdjusted.class), eq(stockTopic));
    }

}
