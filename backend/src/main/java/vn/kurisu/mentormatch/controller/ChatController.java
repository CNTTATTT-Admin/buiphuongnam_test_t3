package vn.kurisu.mentormatch.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import vn.kurisu.mentormatch.dto.request.ChatMessage;
import vn.kurisu.mentormatch.dto.response.ChatMessageResponse;
import vn.kurisu.mentormatch.service.ChatMessageService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage, Principal principal) {
        if (principal == null || principal.getName() == null) {
            return;
        }

        ChatMessageResponse savedMessage = chatMessageService.saveMessage(principal.getName(), chatMessage);

        messagingTemplate.convertAndSend("/topic/private." + savedMessage.getSenderId(), savedMessage);
        messagingTemplate.convertAndSend("/topic/private." + savedMessage.getReceiverId(), savedMessage);
    }
}
