package com.konkuk.gp.client;

import com.konkuk.gp.client.data.ClientRequestData;
import com.konkuk.gp.client.data.ClientResponseData;
import com.konkuk.gp.client.data.TriggerType;
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
@Qualifier("client")
public class ClientSocketHandler extends TextMessageHandler {

    @Autowired
    public ClientSocketHandler(SessionRegistry registry, ChatGptService chatGptService) {
        super(registry, SessionType.CLIENT, chatGptService);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        Message message = null;

        try {
            message = Utils.getObject(textMessage.getPayload(), ClientRequestData.class);
        } catch (Exception e) {
            sendError(session,ErrorMessage.INVALID_MESSAGE_FORMAT);
            return;
        }

        if (!message.getSender().equals(Message.SENDER_CLIENT)) {
            sendError(session,ErrorMessage.INVALID_MESSAGE_FORMAT);
            return;
        }

        ClientRequestData data = (ClientRequestData) message.getData();
        log.info("Received script : " + data.getScript());
        log.info("Received dialogId : " + data.getDialogId());

        String response = chatGptService.simpleChat(data.getScript());

        Message<ClientResponseData> responseData = MessageManager.response(
                ClientResponseData.builder()
                        .dialogId(1L)
                        .isFinish(true)
                        .script(response)
                        .type("daily")
                        .triggerType(TriggerType.BY_USER)
                        .build());
        sendMessage(session, responseData);
    }
}
