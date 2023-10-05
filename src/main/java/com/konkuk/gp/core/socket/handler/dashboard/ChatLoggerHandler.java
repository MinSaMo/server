package com.konkuk.gp.core.socket.handler.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatLoggerHandler extends TextWebSocketHandler {
    private WebSocketSession session;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        this.session = null;
    }

    public void sendLog(List<MultiChatMessage> chats) {
        if (session == null) {
            return;
        }
        String res = null;
        try {
            res = objectMapper.writeValueAsString(chats);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        try {
            session.sendMessage(new TextMessage(res));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
