package com.example.phone_shop.controllers;

import com.example.phone_shop.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin // Để gọi từ Frontend không bị chặn
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/ask")
    public String ask(@RequestBody String question) {
        return chatService.processChat(question);
    }
}