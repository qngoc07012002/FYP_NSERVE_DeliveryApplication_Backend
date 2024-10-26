package nserve.delivery_application_backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/send") // Địa chỉ để gửi tin nhắn
    @SendTo("/topic/messages") // Địa chỉ gửi tin nhắn tới tất cả client
    public ChatMessage sendMessage(ChatMessage message) {
        return message;
    }
}

