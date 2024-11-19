package com.eda.shippingService.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
public abstract class AbstractEntity {
    @Id
    private UUID id = UUID.randomUUID();
}
