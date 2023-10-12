package com.konkuk.gp.core.socket.handler;

import com.konkuk.gp.core.gpt.GptService;
import com.konkuk.gp.core.message.Message;
import com.konkuk.gp.core.message.dto.ai.AiRequestDto;
import com.konkuk.gp.core.socket.DialogManager;
import com.konkuk.gp.core.socket.SessionRegistry;
import com.konkuk.gp.core.socket.SessionType;
import com.konkuk.gp.core.socket.runner.EmergencyCheckRunner;
import com.konkuk.gp.domain.dao.member.MemberChecklist;
import com.konkuk.gp.domain.dto.response.EmergencyCheckDto;
import com.konkuk.gp.global.exception.socket.ErrorCode;
import com.konkuk.gp.global.utils.convert.AIMessageConverter;
import com.konkuk.gp.global.utils.valid.MessageValidator;
import com.konkuk.gp.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Slf4j
@Component
@Qualifier("ai")
public class AiSocketHandler extends TextMessageHandler {

    private final DialogManager dialogManager;
    private final MemberService memberService;
    private final AIMessageConverter aiMessageConverter;
    private final ObjectProvider<EmergencyCheckRunner> emergencyCheckProvider;

    @Autowired
    public AiSocketHandler(
            SessionRegistry registry,
            GptService chatGptService,
            DialogManager dialogManager,
            MessageValidator messageValidator,
            MemberService memberService,
            AIMessageConverter aiMessageConverter, ObjectProvider<EmergencyCheckRunner> emergencyCheckRunnerObjectProvider) {
        super(registry, SessionType.AI, chatGptService, messageValidator);
        this.dialogManager = dialogManager;
        this.memberService = memberService;
        this.aiMessageConverter = aiMessageConverter;
        this.emergencyCheckProvider = emergencyCheckRunnerObjectProvider;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {

        Long memberId = dialogManager.getMemberId();

        Message<AiRequestDto> message;
        try {
            message = convertToAIMessage(textMessage);
        } catch (IllegalArgumentException e) {
            sendError(session, e.getMessage(), ErrorCode.INVALID_MESSAGE);
            log.error("[AI] Conversion error : {}", e.getMessage());
            return;
        } catch (ClassCastException e) {
            sendError(session, e.getMessage(), ErrorCode.INVALID_MESSAGE);
            log.error("[AI] Validation error : {}", e.getMessage());
            return;
        }

        AiRequestDto data = message.getData();
        String caption = data.getCaption();
        log.info("[AI] Received caption : " + caption);

        EmergencyCheckDto checkedEmergency = chatGptService.checkEmergency(caption);
        if (isEmergency(checkedEmergency)) {
            new Thread(emergencyCheckProvider.getObject()).start();
        }

        List<MemberChecklist> todolist = memberService.getTodolist(memberId);
        chatGptService.checkCompletedTodolist(caption, memberId);

        Message<String> response = Message.of("OK", Message.STATUS_OK);
        sendMessage(session, response);
    }

    private Message<AiRequestDto> convertToAIMessage(TextMessage textMessage) {
        Message<AiRequestDto> message = aiMessageConverter.convert(textMessage);
        messageValidator.validate(message);
        return message;
    }

    private boolean isEmergency(EmergencyCheckDto checkedEmergency) {
        return checkedEmergency.isDetected();
    }
}
