package com.konkuk.daila.service.dialog;

import com.konkuk.daila.domain.dao.dialog.DialogHistory;
import com.konkuk.daila.domain.dao.dialog.DialogHistoryRepository;
import com.konkuk.daila.domain.dto.request.UserInformationGenerateDto;
import com.konkuk.daila.domain.dto.response.UserInformationResponseDto;
import com.konkuk.daila.global.logger.DashboardLogger;
import com.konkuk.daila.global.logger.UserInformationLogProperty;
import com.konkuk.daila.service.*;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DialogService {

    private final MemberService memberService;
    private final GptService gptService;
    private final PromptManager promptManager;
    private final DialogHistoryRepository dialogHistoryRepository;

    private final UserInformationLogProperty userLogProperty;
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
        log.info("[DIALOG] END DIALOG");

        /*
        Reset GPT Dialog
         */
        gptHistory = new ArrayList<>();
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

        Map<String, String> systemParamMap = new HashMap<>();
        systemParamMap.put("$currentTime", LocalDateTime.now().toString());
        String systemScript = promptManager.setPromptParams(informationPrompt.getScript(), systemParamMap);

        ChatCompletionRequest request = wrapWithHistory(gptService.request())
                .addSystemMessage(systemScript)
                .topP(informationPrompt.getTopP())
                .temperature(informationPrompt.getTemperature())
                .build();

        UserInformationGenerateDto response = gptService.ask(request, UserInformationGenerateDto.class);
        memberService.saveInformation(response, memberId);
        userLogProperty.load();
        logger.sendUserInformationLog();
    }

    public ChatCompletionRequest.Builder wrapWithHistory(ChatCompletionRequest.Builder builder) {
        for (Message msg : currentHistory) {
            String script = msg.getScript();
            if (msg.isUserMessage()) {
                builder.addUserMessage(script);
            } else if(msg.isAssistantMessage()) {
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
