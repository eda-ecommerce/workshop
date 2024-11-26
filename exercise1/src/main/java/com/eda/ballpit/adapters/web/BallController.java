package com.eda.ballpit.adapters.web;

import com.eda.ballpit.application.service.BallService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/throwString")
    private void create(@RequestBody String color){
        ballService.throwBallString(color);
    }

    @PostMapping("/throwJson")
    private void createJson(@RequestBody String color){
        ballService.throwBallJson(color);
    }

    @PostMapping("/throwMoreStrings")
    private void createMore(@RequestBody Integer amount){
        var colors = new String[]{"red", "blue", "green", "pink", "yellow"};
        for(int i = 0; i < amount; i++){
            ballService.throwBallString(colors[i % colors.length]);
        }
    }
}
