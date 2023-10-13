package com.konkuk.gp.core.socket.handler;

import com.konkuk.gp.core.gpt.GptService;
import com.konkuk.gp.core.gpt.enums.ChatType;
import com.konkuk.gp.core.message.Message;
import com.konkuk.gp.core.message.MessageUtils;
import com.konkuk.gp.core.message.dto.client.ClientRequestDto;
import com.konkuk.gp.core.message.dto.client.ClientResponseDto;
import com.konkuk.gp.core.message.dto.client.TriggerType;
import com.konkuk.gp.core.socket.DialogManager;
import com.konkuk.gp.core.socket.SessionRegistry;
import com.konkuk.gp.core.socket.SessionType;
import com.konkuk.gp.domain.dto.response.DialogResponseDto;
import com.konkuk.gp.global.Utils;
import com.konkuk.gp.global.exception.socket.ErrorCode;
import com.konkuk.gp.global.utils.convert.ClientMessageConverter;
import com.konkuk.gp.global.utils.valid.MessageValidator;
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
    private final ClientMessageConverter converter;

    @Autowired
    public ClientSocketHandler(
            SessionRegistry registry,
            GptService chatGptService,
            DialogManager dialogManager,
            MessageValidator messageValidator,
            ClientMessageConverter converter) {
        super(registry, SessionType.CLIENT, chatGptService, messageValidator);
        this.dialogManager = dialogManager;
        this.converter = converter;
    }


    /**
     * Decorator
     * @param session     Client Session
     * @param textMessage Client Message
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {

        Long memberId = dialogManager.getMemberId();
        Long dialogId = dialogManager.startDialog();

        Message<ClientRequestDto> message;
        try {
            message = convertToClientMessage(textMessage);
        } catch (IllegalArgumentException e) {
            sendError(session, e.getMessage(), ErrorCode.INVALID_MESSAGE);
            log.error("[CLIENT] Conversion error : {}", e.getMessage());
            return;
        } catch (ClassCastException e) {
            sendError(session, e.getMessage(), ErrorCode.INVALID_MESSAGE);
            log.error("[CLIENT] Validation error : {}", e.getMessage());
            return;
        }

        ClientRequestDto data = message.getData();
        log.info("[CLIENT] Received script : {}", data.getScript());
        log.info("[CLIENT] Received dialogId : {}", data.getDialogId());

        if (!data.getIsReal()) {
            adminProcess(data);
            return;
        }

        ChatType chatType = chatGptService.determineIntense(data.getScript());
        List<MultiChatMessage> currentHistory = dialogManager.getCurrentHistory();
        DialogResponseDto res = chatGptService.ask(data.getScript(), chatType, memberId, currentHistory);

        List<MultiChatMessage> chat = new ArrayList<>();
        chat.add(new MultiChatMessage("user", data.getScript()));

        Message<ClientResponseDto> responseData = MessageUtils.response(
                ClientResponseDto.builder()
                        .dialogId(dialogId)
                        .script(res.response())
                        .type(chatType.getName())
                        .triggerType(TriggerType.BY_USER)
                        .build());

        sendMessage(session, responseData);

        chat.add(new MultiChatMessage("assistant", res.response()));
        dialogManager.addMessage(chat);
    }

    private void adminProcess(ClientRequestDto data) {
        List<MultiChatMessage> chat = new ArrayList<>();
        chat.add(new MultiChatMessage("user", data.getScript()));
        dialogManager.addMessage(chat);
        dialogManager.saveDialogHistory();
        dialogManager.sendUserInfo();
    }

    public Message<ClientRequestDto> convertToClientMessage(TextMessage textMessage) {
        Message<ClientRequestDto> message = converter.convert(textMessage);
        messageValidator.validate(message);
        return message;
    }

    /**
     * Admin API 를 위해 필요한 함수
     */
    public void sendAdminMessage(WebSocketSession session, Message<String> message) {
        try {
            session.sendMessage(new TextMessage(Utils.getString(message)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
