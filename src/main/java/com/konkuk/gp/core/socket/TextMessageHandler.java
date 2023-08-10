package com.konkuk.gp.core.socket;

import com.konkuk.gp.core.message.Message;
import com.konkuk.gp.core.message.MessageManager;
import com.konkuk.gp.global.Utils;
import com.konkuk.gp.global.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
public class TextMessageHandler extends TextWebSocketHandler {
    protected final SessionRegistry registry;
    protected final SessionType sessionType;

     protected void sendError(WebSocketSession session, ErrorMessage errMsg) {
        try {
            session.sendMessage(new TextMessage(Utils.getString(MessageManager.error(errMsg))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> void sendMessage(WebSocketSession session, Message<T> message) {
        try {
            session.sendMessage(new TextMessage(Utils.getString(message)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (registry.exist(sessionType)) {
            sendError(session, ErrorMessage.SOCKET_ALREADY_USED);
            session.close();
            return;
        }

        try {
            registry.register(session, sessionType);
        } catch (IllegalArgumentException e) {
            sendError(session, ErrorMessage.SOCKET_ALREADY_USED);
            session.close();
            return;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        try {
            registry.remove(session, sessionType);
        } catch (IllegalArgumentException e) {
            // TODO : log 찍기
//            sendError(session, ErrorMessage.SOCKET_CLOSE_FAILED_NOT_FOUND);
        } catch (IllegalStateException e) {
            // TODO : log 찍기
//            sendError(session, ErrorMessage.SOCKET_CLOSE_FAILED_INVALID);
        } finally {
            session.close();
        }
    }
}
