package com.konkuk.gp.core.socket.handler;

import com.konkuk.gp.core.gpt.GptService;
import com.konkuk.gp.core.gpt.enums.ChatType;
import com.konkuk.gp.core.message.Message;
import com.konkuk.gp.core.message.MessageManager;
import com.konkuk.gp.core.message.dto.ai.AiRequestDto;
import com.konkuk.gp.core.message.dto.client.ClientResponseDto;
import com.konkuk.gp.core.message.dto.client.TriggerType;
import com.konkuk.gp.core.socket.SessionRegistry;
import com.konkuk.gp.core.socket.SessionType;
import com.konkuk.gp.global.Utils;
import com.konkuk.gp.global.exception.socket.ErrorMessage;
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
    public AiSocketHandler(SessionRegistry registry, GptService chatGptService) {
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
        ChatType chatType = chatGptService.determineIntense(data.getCaption());

        /***
         * @function
         * 1. 사용자가 todolist에서 한 행위가 있는가 체크
         * 2. 사용자가 하고 있는 행위가 조언이 필요한 행위인가 체크
         *  -> 2번이면 대화 넘기면 되고
         * 3. 과연 그러면 daily를 지원할건가
         *  3-1. 서있거나, 앉아있거나, 누워있거나
         *      -> 룰 기반으로 특정 행동 인지 시 확인
         *  3-2. 간섭정도를 정할 수 있는가
         * 4. emergency
         *  -> 넘어졌다, 쓰러졌다라는 캡션이 나오면 클라이언트에게 위급상황 확인요청 전송
         *  -> 이것도 룰 기반의 특정 행동양식 인지
         *  -> 클라이언트가 알아서 하다가, 나한테 위급상황 매뉴얼 수행 요청
         */
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
