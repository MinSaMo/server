package com.konkuk.gp.controller.stomp;

import com.konkuk.gp.controller.stomp.dto.ai.AiRequestDto;
import com.konkuk.gp.controller.stomp.dto.client.ClientRequestDto;
import com.konkuk.gp.controller.stomp.dto.client.ClientResponseDto;
import com.konkuk.gp.controller.stomp.dto.client.TriggerType;
import com.konkuk.gp.domain.dto.response.DialogResponseDto;
import com.konkuk.gp.domain.dto.response.EmergencyCheckDto;
import com.konkuk.gp.global.logger.DashboardLogger;
import com.konkuk.gp.global.message.Message;
import com.konkuk.gp.global.thread.EmergencyCheckRunner;
import com.konkuk.gp.global.validation.MessageValid;
import com.konkuk.gp.service.GptService;
import com.konkuk.gp.service.dialog.DialogManager;
import com.konkuk.gp.service.dialog.TimerStart;
import com.konkuk.gp.service.enums.ChatType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageController {

    private final GptService gptService;
    private final DialogManager dialogManager;
    private final ObjectProvider<EmergencyCheckRunner> provider;

    private final DashboardLogger logger;

    @MessageMapping("/script")
    @SendTo("/sub/reply")
    @TimerStart
    @MessageValid
    public ClientResponseDto dialogWithScript(
            Message<ClientRequestDto> dto
    ) {
        long start = System.currentTimeMillis();
        Long memberId = dialogManager.getMemberId();
        Long dialogId = dialogManager.startDialog();

        ClientRequestDto data = dto.getData();
        String script = data.getScript();
        logger.sendScriptLog(script, memberId, dialogId);
        ChatType chatType = gptService.determineIntense(script);
        logger.sendIntenseLog(chatType);

        DialogResponseDto reply = gptService.ask(
                script,
                chatType,
                memberId,
                dialogManager.getCurrentHistory()
        );

        long time = System.currentTimeMillis() - start;
        dialogManager.addMessage(script, reply.response());
        return ClientResponseDto.builder()
                .script(reply.response())
                .triggerType(TriggerType.BY_USER)
                .dialogId(dialogId)
                .type(chatType.getName())
                .isFinish(false)
                .time(time)
                .build();
    }

    @MessageValid
    @MessageMapping("/caption")
    @SendTo(value = "/sub/reply")
    public Message<ClientResponseDto> dialogWithCaption(
            Message<AiRequestDto> dto
    ) {
        Long memberId = dialogManager.getMemberId();
        Long dialogId = dialogManager.startDialog();

        AiRequestDto data = dto.getData();
        String caption = data.getCaption();
        /**
         * 1. 응급상황 판단
         * 2. 대화가 필요한 상황인지 판단
         */
        EmergencyCheckDto emergencyResult = gptService.checkEmergency(caption);
        if (emergencyResult.isDetected()) {
            // new Thread(provider.getObject()).start(); 수정
        }
        gptService.checkCompletedTodolist(caption, memberId);
        return null;
    }
}
