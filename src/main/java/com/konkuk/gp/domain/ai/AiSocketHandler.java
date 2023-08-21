package com.konkuk.gp.domain.ai;

import com.konkuk.gp.core.gpt.enums.ChatType;
import com.konkuk.gp.domain.ai.dto.AiRequestDto;
import com.konkuk.gp.domain.client.dto.ClientResponseDto;
import com.konkuk.gp.domain.client.dto.TriggerType;
import com.konkuk.gp.core.gpt.ChatGptService;
import com.konkuk.gp.core.message.Message;
import com.konkuk.gp.core.message.MessageManager;
import com.konkuk.gp.core.socket.SessionRegistry;
import com.konkuk.gp.core.socket.SessionType;
import com.konkuk.gp.core.socket.TextMessageHandler;
import com.konkuk.gp.global.Utils;
import com.konkuk.gp.global.exception.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@Qualifier("ai")
public class AiSocketHandler extends TextMessageHandler {
    @Autowired
    public AiSocketHandler(SessionRegistry registry, ChatGptService chatGptService) {
        super(registry, SessionType.AI, chatGptService);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        Message<AiRequestDto> message;
        try {
            message = Utils.getObject(textMessage.getPayload(), AiRequestDto.class);
        } catch (Exception e) {
            sendError(session, ErrorMessage.INVALID_MESSAGE_FORMAT);
            return;
        }

        if (!message.getSender().equals(Message.SENDER_AI)) {
            sendError(session, ErrorMessage.INVALID_MESSAGE_FORMAT);
            return;
        }

        AiRequestDto data = message.getData();
        log.info("[AI] Received caption : " + data.getCaption());

        String response = chatGptService.simpleChat(data.getCaption());
        int dialogType = chatGptService.determineIntense(data.getCaption());
        ChatType type = ChatType.of(dialogType);

        Message<ClientResponseDto> responseData = MessageManager.response(
                ClientResponseDto.builder()
                        .dialogId(1L)
                        .isFinish(true)
                        .script(response)
                        .type("advice")
                        .triggerType(TriggerType.BY_CAPTION)
                        .build());

        try {
            WebSocketSession client = registry.getSession(SessionType.CLIENT)
                    .orElseThrow(IllegalAccessError::new);
            sendMessage(client, responseData);
            log.info("[AI] SEND RESPONSE TO CLIENT:" + response);
        } catch (IllegalAccessError e) {
            sendError(session, ErrorMessage.CLIENT_SOCKET_NOT_FOUND);
        }
    }
}
