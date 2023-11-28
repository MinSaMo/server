package com.konkuk.daila.service.dialog;

import com.konkuk.daila.domain.dao.dialog.DialogHistory;
import com.konkuk.daila.domain.dao.dialog.DialogHistoryRepository;
import com.konkuk.daila.domain.dto.request.UserInformationGenerateDto;
import com.konkuk.daila.domain.dto.response.UserInformationResponseDto;
import com.konkuk.daila.global.logger.DashboardLogger;
import com.konkuk.daila.service.GptService;
import com.konkuk.daila.service.MemberService;
import com.konkuk.daila.service.Prompt;
import com.konkuk.daila.service.PromptManager;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DialogService {

    private final MemberService memberService;
    private final GptService gptService;
    private final PromptManager promptManager;
    private final DialogHistoryRepository dialogHistoryRepository;

    private final DashboardLogger logger;

    private boolean isRun;
    @Getter
    private Long dialogId;
    @Getter
    @Setter
    private Long memberId;
    @Getter
    private List<Message> currentHistory;

    @Getter
    @Setter
    private List<Message> gptHistory;
    private Long sequence;

    @PostConstruct
    public void init() {
        isRun = false;
        dialogId = 0L;
        memberId = 1L;
        sequence = 1L;
        // LLM
        gptHistory = new ArrayList<>();
    }

    public void addChatToGpt(Message message) {
        gptHistory.add(message);
    }

    public Long startDialog() {
        if (hasDialog()) return dialogId;
        if (isRun || memberId == null) {
            throw new RuntimeException("INVALID STATE : start dialog");
        }
        isRun = true;
        currentHistory = new ArrayList<>();
        return generateDialogId();
    }

    @Transactional
    public void endDialog() {
        if (!isValidDialogId(dialogId) || !isRun) {
            throw new RuntimeException("INVALID STATE : end dialog");
        }
        isRun = false;
        saveDialogHistory();
        generateUserInformation();
        gptHistory = new ArrayList<>();
        log.info("[DIALOG] END DIALOG");
    }

    public boolean hasDialog() {
        return isRun;
    }

    public int addMessage(List<Message> chatMessages) {
        if (!isRun) return -1;
        this.currentHistory.addAll(chatMessages);
//        chatLoggerHandler.sendLog(currentHistory);
        return Math.toIntExact(sequence++);
    }

    public int addMessage(String user, String assistant) {
        if (!isRun) return -1;
        this.currentHistory.add(Message.ofUser(user));
        this.currentHistory.add(Message.ofAssistant(assistant));
        return Math.toIntExact(sequence++);
    }

    public int addMessage(Message chatMessages) {
        if (!isRun) return -1;
        this.currentHistory.add(chatMessages);
        return Math.toIntExact(sequence);
    }

    /**
     * save history into mongo
     */
    public void saveDialogHistory() {
        DialogHistory dialogHistory = DialogHistory.builder()
                .memberId(memberId)
                .chats(currentHistory)
                .build();
        dialogHistoryRepository.save(dialogHistory);
    }

    /**
     * generate user information and save into rdb
     */
    @Transactional
    public void generateUserInformation() {

        Prompt informationPrompt = promptManager.getInfoPrompt();
        Prompt duplicatePrompt = promptManager.getDuplicatePrompt();

//        Map<String, String> systemParamMap = new HashMap<>();
//        systemParamMap.put("$currentTime", LocalDateTime.now().toString());
//        String systemScript = promptManager.setPromptParams(informationPrompt.getScript());

        ChatCompletionRequest request = wrapWithHistory(gptService.request())
                .addSystemMessage(informationPrompt.getScript())
                .topP(informationPrompt.getTopP())
                .temperature(informationPrompt.getTemperature())
                .build();

        UserInformationGenerateDto response = gptService.askToSub(request, UserInformationGenerateDto.class);
        String duplicatedString = memberService.generateRawInformationString(response, memberId);

        request = gptService.request()
                .addSystemMessage(duplicatePrompt.getScript())
                .topP(duplicatePrompt.getTopP())
                .temperature(duplicatePrompt.getTemperature())
                .addUserMessage(duplicatedString)
                .build();

        response = gptService.askToSub(request, UserInformationGenerateDto.class);
        memberService.saveInformation(response, memberId);
        logger.sendUserInformationLog();
    }

    public ChatCompletionRequest.Builder wrapWithHistory(ChatCompletionRequest.Builder builder) {
        for (Message msg : currentHistory) {
            String script = msg.getScript();
            if (msg.isUserMessage()) {
                builder.addUserMessage(script);
            } else if (msg.isAssistantMessage()) {
                builder.addAssistantMessage(script);
            }
        }
        return builder;
    }

    public ChatCompletionRequest.Builder wrapWithLLMHistory(ChatCompletionRequest.Builder builder) {
        for (Message msg : gptHistory) {
            String script = msg.getScript();
            if (msg.isUserMessage()) {
                builder.addUserMessage(script);
            } else if (msg.isAssistantMessage()) {
                builder.addAssistantMessage(script);
            }
        }
        return builder;
    }

    public void sendUserInfo() {
        UserInformationResponseDto info = memberService.getInformation(memberId);
//        userInformationHandler.sendInfo(info);
    }

    private Long generateDialogId() {
        return ++dialogId;
    }

    public boolean isValidDialogId(Long dialogId) {
        return this.dialogId.equals(dialogId);
    }
}
