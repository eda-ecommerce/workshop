package com.eda.ballpit.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonSerialize
public class Ball {
    @Id
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("color")
    private String color;
    public Ball(UUID id, String color) {
        this.id = id;
        this.color = color;
    }
    public Ball(String color) {
        this.id = UUID.randomUUID();
        this.color = color;
    }

    @Override
    public String toString() {
        return """
                Ball{
                    id=%s,
                    color='%s'
                }
                """.formatted(id, color);
    }
}