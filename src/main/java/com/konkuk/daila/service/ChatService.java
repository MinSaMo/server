package com.konkuk.daila.service;

import com.konkuk.daila.domain.dto.response.DialogResponseDto;
import com.konkuk.daila.domain.dto.response.IntenseResponseDto;
import com.konkuk.daila.global.logger.DashboardLogger;
import com.konkuk.daila.service.dialog.DialogService;
import com.konkuk.daila.service.dialog.Message;
import com.konkuk.daila.service.enums.ChatType;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatService {

    private final PromptManager promptManager;
    private final GptService gptService;
    private final DialogService dialogService;
    private final MemberService memberService;
    private final DashboardLogger logger;

    // response with LLM
    public String responseWithLLM(String script) {
        dialogService.addChatToGpt(Message.ofUser(script));
        ChatCompletionRequest request = dialogService.wrapWithLLMHistory(gptService.request()
                        .addSystemMessage(promptManager.getNormalLLMPrompt().getScript()))
                .build();
        DialogResponseDto result = gptService.askToSub(request, DialogResponseDto.class);
        dialogService.addChatToGpt(Message.ofAssistant(result.response()));
        return result.response();
    }

    // determine intention
    public ChatType determineIntention(String script) {
        Prompt intensePrompt = promptManager.getIntensePrompt();
        ChatCompletionRequest intenseRequest = gptService.request()
                .addSystemMessage(intensePrompt.getScript())
                .topP(intensePrompt.getTopP())
                .temperature(intensePrompt.getTemperature())
                .build();
        IntenseResponseDto response = gptService.ask(intenseRequest, IntenseResponseDto.class);
        return ChatType.of(response.answerTypeIndex());
    }

    // response with daily
    public DialogResponseDto responseWithDaily(String script) {
        Long memberId = dialogService.getMemberId();
        Prompt dailyPrompt = promptManager.getDailyPrompt();

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("$message", script);
        paramMap.put("$userInformation", memberService.getInformationString(memberId));

        String prompt = promptManager.setPromptParams(dailyPrompt.getScript(), paramMap);
        logger.sendPromptLog(prompt);

        ChatCompletionRequest request = dialogService.wrapWithHistory(gptService.request())
                .addSystemMessage(prompt)
                .topP(dailyPrompt.getTopP())
                .temperature(dailyPrompt.getTemperature())
                .build();

        DialogResponseDto response = gptService.ask(request, DialogResponseDto.class);
        dialogService.addMessage(script, response.response());
        return response;
    }
}
