package com.jacob.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.jacob.backend.data.Model.Game;

@Controller
public class GameSocket {

    @MessageMapping("/game/{gameId}")
    @SendTo("/game/{gameId}")
    public Game hello(Game msg) {
        return msg;
    }
}
