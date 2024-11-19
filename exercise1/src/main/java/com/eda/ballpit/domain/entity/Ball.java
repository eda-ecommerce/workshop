package com.eda.ballpit.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ball {
    private String color;
    @Id
    private UUID id;

    public Ball(String color) {
        this.id = UUID.randomUUID();
        this.color = color;
    }
    @Override
    public String toString() {
        return "This is a "+color+" ball";
    }
}