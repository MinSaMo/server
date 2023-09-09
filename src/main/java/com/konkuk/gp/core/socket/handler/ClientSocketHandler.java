package com.konkuk.gp.core.socket.handler;

import com.konkuk.gp.core.gpt.GptService;
import com.konkuk.gp.core.gpt.dto.DialogResponseDto;
import com.konkuk.gp.core.gpt.enums.ChatType;
import com.konkuk.gp.core.message.Message;
import com.konkuk.gp.core.message.MessageManager;
import com.konkuk.gp.core.message.dto.client.ClientRequestDto;
import com.konkuk.gp.core.message.dto.client.ClientResponseDto;
import com.konkuk.gp.core.message.dto.client.TriggerType;
import com.konkuk.gp.core.socket.DialogManager;
import com.konkuk.gp.core.socket.SessionRegistry;
import com.konkuk.gp.core.socket.SessionType;
import com.konkuk.gp.global.Utils;
import com.konkuk.gp.global.exception.socket.ErrorMessage;
import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Qualifier("client")
public class ClientSocketHandler extends TextMessageHandler {

    private final DialogManager dialogManager;

    @Autowired
    public ClientSocketHandler(SessionRegistry registry, GptService chatGptService, DialogManager dialogManager) {
        super(registry, SessionType.CLIENT, chatGptService);
        this.dialogManager = dialogManager;
    }

    public void sendAdminMessage(WebSocketSession session, Message<String> message) {
        try {
            session.sendMessage(new TextMessage(Utils.getString(message)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {

        Long memberId = dialogManager.getMemberId();
        Message message = null;

        Long dialogId;
        if (!dialogManager.hasDialog()) {
            dialogId = dialogManager.startDialog();
        } else {
            dialogId = dialogManager.getDialogId();
        }

        try {
            message = Utils.getObject(textMessage.getPayload(), ClientRequestDto.class);
        } catch (Exception e) {
            sendError(session, ErrorMessage.INVALID_MESSAGE_FORMAT);
            return;
        }

        if (!message.getSender().equals(Message.SENDER_CLIENT)) {
            sendError(session, ErrorMessage.INVALID_MESSAGE_FORMAT);
            return;
        }

        ClientRequestDto data = (ClientRequestDto) message.getData();
        log.info("[CLIENT] Received script : {}", data.getScript());
        log.info("[CLIENT] Received dialogId : {}", data.getDialogId());

        ChatType chatType = chatGptService.determineIntense(data.getScript());
        List<MultiChatMessage> currentHistory = dialogManager.getCurrentHistory();
        DialogResponseDto res = chatGptService.ask(data.getScript(), chatType, memberId, currentHistory);

        List<MultiChatMessage> chat = new ArrayList<>();
        chat.add(new MultiChatMessage("user", data.getScript()));

        Message<ClientResponseDto> responseData = MessageManager.response(
                ClientResponseDto.builder()
                        .dialogId(dialogId)
                        .script(res.response())
                        .type(chatType.getName())
                        .triggerType(TriggerType.BY_USER)
                        .build());

        sendMessage(session, responseData);

        chat.add(new MultiChatMessage("assistant", res.response()));
        dialogManager.addMessage(chat);
        dialogManager.saveDialogHistory();
        dialogManager.generateUserInformation();
    }
}
