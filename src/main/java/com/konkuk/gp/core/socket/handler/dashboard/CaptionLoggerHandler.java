package com.konkuk.gp.core.socket.handler.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CaptionLoggerHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private WebSocketSession session;
    private List<CaptionDto> captionList;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        this.session = null;
    }

    @PostConstruct
    private void init() {
        captionList = new ArrayList<>();
    }

    public void sendCaption() {
        if (session == null) {
            return;
        }
        String res = null;
        try {
            res = objectMapper.writeValueAsString(captionList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        try {
            session.sendMessage(new TextMessage(res));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCaption(String caption) {
        this.captionList.add(new CaptionDto(caption, LocalDateTime.now()));
        sendCaption();
    }

}
