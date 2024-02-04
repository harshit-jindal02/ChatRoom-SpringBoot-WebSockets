package com.project.chatapplication.config;

import com.project.chatapplication.chat.ChatMessage;
import com.project.chatapplication.chat.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String user = (String) accessor.getSessionAttributes().get("user");
        if (user != null) {
            log.info("Disconnected user " + user);
            var chatMessage = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .sender(user)
                    .build();
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}