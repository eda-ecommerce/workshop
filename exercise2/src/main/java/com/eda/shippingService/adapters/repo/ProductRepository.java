package com.eda.shippingService.adapters.repo;

import com.eda.shippingService.domain.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, UUID> {
}
