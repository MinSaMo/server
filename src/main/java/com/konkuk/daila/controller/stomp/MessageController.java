package com.konkuk.daila.controller.stomp;

import com.konkuk.daila.controller.stomp.dto.ai.AiRequestDto;
import com.konkuk.daila.controller.stomp.dto.client.ClientRequestDto;
import com.konkuk.daila.controller.stomp.dto.client.ClientResponseDto;
import com.konkuk.daila.domain.dto.response.DialogResponseDto;
import com.konkuk.daila.domain.dto.response.EmergencyCheckDto;
import com.konkuk.daila.global.logger.DashboardLogger;
import com.konkuk.daila.global.thread.EmergencyCheckRunner;
import com.konkuk.daila.global.thread.TodolistCheckRunner;
import com.konkuk.daila.global.validation.MessageValid;
import com.konkuk.daila.service.BehaviorService;
import com.konkuk.daila.service.ChatService;
import com.konkuk.daila.service.dialog.DialogService;
import com.konkuk.daila.service.dialog.TimerStart;
import com.konkuk.daila.service.enums.ChatType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Random;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageController {

    private final ChatService chatService;
    private final DialogService dialogService;
    private final BehaviorService behaviorService;
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
        String response = chatService.responseWithLLM(script);
        long time = System.currentTimeMillis() - start;
        if (time <= 10000) {
            try {
                Random random = new Random();
                int randomInterval = random.nextInt(7000) + 5000;
                time += randomInterval;
                log.info("[GPT] interval : {}", randomInterval);
                Thread.sleep(randomInterval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return ClientResponseDto.builder()
                .type(ChatType.LLM.getName())
                .dialogId(-1L)
                .script(response)
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
        Long memberId = dialogService.getMemberId();
        Long dialogId = dialogService.startDialog();

        String script = dto.getScript();
        logger.sendScriptLog(script, memberId, dialogId);

        ChatType chatType = chatService.determineIntention(script);
        logger.sendIntenseLog(chatType);

        if (chatType.equals(ChatType.SERVER_ERR)) {
            long time = System.currentTimeMillis() - start;
            return ClientResponseDto.builder()
                    .script("Sorry, Server Error on Remote GPT. Please resend message.")
                    .dialogId(dialogId)
                    .type(chatType.getName())
                    .time(time)
                    .build();
        }

        DialogResponseDto reply = chatService.responseWithDaily(script);
        long time = System.currentTimeMillis() - start;
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
        Long memberId = dialogService.getMemberId();

        String caption = dto.getCaption();
        logger.sendCaptionLog(caption, memberId);

        EmergencyCheckDto emergencyResult = behaviorService.checkEmergency(caption);
        if (emergencyResult.isDetected()) {
            EmergencyCheckRunner emergencyChecker = getEmergencyChecker(caption);
            startBackgroundJob(emergencyChecker);
            return null;
        }

        TodolistCheckRunner todoChecker = getTodoChecker(caption, memberId);
        startBackgroundJob(todoChecker);

        // TODO : 말을 걸지 판단하는 Script 필요함
        DialogResponseDto response = chatService.responseWithDaily(caption);
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
