package com.eda.ballpit.adapters.web;

import com.eda.ballpit.application.service.BallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ball")
public class BallController {
    private final BallService ballService;
    @Autowired
    public BallController(BallService ballService) {
        this.ballService = ballService;
    }

    @PostMapping("/throw")
    private void create(@RequestBody String color){
        ballService.throwBall(color);
    }
}
