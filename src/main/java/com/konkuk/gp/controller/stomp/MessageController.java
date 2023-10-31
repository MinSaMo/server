package com.konkuk.gp.controller.stomp;

import com.konkuk.gp.controller.stomp.dto.ai.AiRequestDto;
import com.konkuk.gp.controller.stomp.dto.client.ClientRequestDto;
import com.konkuk.gp.controller.stomp.dto.client.ClientResponseDto;
import com.konkuk.gp.domain.dto.response.DialogResponseDto;
import com.konkuk.gp.domain.dto.response.EmergencyCheckDto;
import com.konkuk.gp.global.logger.DashboardLogger;
import com.konkuk.gp.global.thread.EmergencyCheckRunner;
import com.konkuk.gp.global.thread.TodolistCheckRunner;
import com.konkuk.gp.global.validation.MessageValid;
import com.konkuk.gp.service.GptService;
import com.konkuk.gp.service.dialog.DialogManager;
import com.konkuk.gp.service.dialog.TimerStart;
import com.konkuk.gp.service.enums.ChatType;
import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageController {

    private final GptService gptService;
    private final DialogManager dialogManager;
    private final ObjectProvider<EmergencyCheckRunner> emergencyCheckerProvider;
    private final ObjectProvider<TodolistCheckRunner> todolistCheckerProvider;

    private final DashboardLogger logger;

    @MessageMapping("/gpt")
    @SendTo("/topic/service/reply_gpt")
    public ClientResponseDto dialogWithLLM(
            ClientResponseDto dto
    ) {
        long start = System.currentTimeMillis();
        String script = dto.getScript();
        MultiChatMessage userMessage = new MultiChatMessage("user", script);
        dialogManager.addChatToGpt(userMessage);
        List<MultiChatMessage> history = dialogManager.getGptHistory();

        String reply = gptService.responseWithLLM(history);
        long time = System.currentTimeMillis() - start;
        return ClientResponseDto.builder()
                .type(ChatType.LLM.getName())
                .dialogId(-1L)
                .script(reply)
                .time(time)
                .build();
    }

    @MessageMapping("/script")
    @SendTo("/topic/service/reply")
    @TimerStart
    @MessageValid
    public ClientResponseDto dialogWithScript(
            ClientRequestDto dto
    ) {
        long start = System.currentTimeMillis();
        Long memberId = dialogManager.getMemberId();
        Long dialogId = dialogManager.startDialog();

        String script = dto.getScript();
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
        logger.sendReplyLog(reply.response());
        return ClientResponseDto.builder()
                .script(reply.response())
                .dialogId(dialogId)
                .type(chatType.getName())
                .time(time)
                .build();
    }

    @MessageValid
    @MessageMapping("/caption")
    @SendTo(value = "/topic/service/reply")
    public ClientResponseDto dialogWithCaption(
            AiRequestDto dto
    ) {
        long start = System.currentTimeMillis();
        Long memberId = dialogManager.getMemberId();
        Long dialogId = dialogManager.startDialog();

        String caption = dto.getCaption();
        logger.sendCaptionLog(caption, memberId);

        EmergencyCheckDto emergencyResult = gptService.checkEmergency(caption);
        if (emergencyResult.isDetected()) {
            EmergencyCheckRunner emergencyChecker = getEmergencyChecker(caption);
            startBackgroundJob(emergencyChecker);
            return null;
        }

        TodolistCheckRunner todoChecker = getTodoChecker(caption, memberId);
        startBackgroundJob(todoChecker);

        // TODO : 말을 걸지 판단하는 Script 필요함
        // 현재 구현은 ADVICE 로 내어줌
        DialogResponseDto response = gptService.ask(caption, ChatType.ADVICE, memberId, dialogManager.getCurrentHistory());
        long time = System.currentTimeMillis() - start;
        logger.sendCaptionReplyLog(response.response());
        return ClientResponseDto.builder()
                .script(response.response())
                .dialogId(-1L)
                .type(ChatType.ADVICE.getName())
                .time(time)
                .build();
    }

    @MessageMapping("/user-info")
    public void sendUserInformation() {
        logger.sendUserInformationLog();
    }

    private void startBackgroundJob(Runnable runnable) {
        new Thread(runnable).start();
    }

    private EmergencyCheckRunner getEmergencyChecker(String caption) {
        EmergencyCheckRunner runner = emergencyCheckerProvider.getObject();
        runner.setCaption(caption);
        return runner;
    }

    private TodolistCheckRunner getTodoChecker(String caption, Long memberId) {
        TodolistCheckRunner runner = todolistCheckerProvider.getObject();
        runner.setParam(caption, memberId);
        return runner;
    }
}
