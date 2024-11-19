package com.eda.shippingService.domain.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer extends AbstractEntity{
    private String firstName;
    private String lastName;
}
