package com.eda.shippingService.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ball {
    private String color;
    @Getter
    @Setter
    @Id
    private Long id;

    public Ball(String color) {
        this.color = color;
    }}
