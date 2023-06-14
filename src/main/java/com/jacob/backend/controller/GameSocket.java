package com.jacob.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.jacob.backend.data.DTO.MessageDTO;

@Controller
public class GameSocket {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public MessageDTO hello(MessageDTO msg) {
        return msg;
    }
}
