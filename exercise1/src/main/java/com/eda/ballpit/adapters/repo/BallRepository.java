package com.eda.ballpit.adapters.repo;

import com.eda.ballpit.domain.entity.Ball;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface BallRepository extends CrudRepository<Ball, UUID> {
    List<Ball> findByColor(String color);
}
