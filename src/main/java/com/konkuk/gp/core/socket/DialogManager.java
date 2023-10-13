package com.konkuk.gp.core.socket;

import com.konkuk.gp.core.gpt.GptService;
import com.konkuk.gp.core.socket.aop.TimerStart;
import com.konkuk.gp.core.socket.handler.dashboard.ChatLoggerHandler;
import com.konkuk.gp.core.socket.handler.dashboard.UserInformationHandler;
import com.konkuk.gp.domain.dao.dialog.DialogHistory;
import com.konkuk.gp.domain.dao.dialog.DialogHistoryRepository;
import com.konkuk.gp.domain.dto.request.UserInformationGenerateDto;
import com.konkuk.gp.domain.dto.response.UserInformationResponseDto;
import com.konkuk.gp.service.MemberService;
import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
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
public class DialogManager {

    private final MemberService memberService;
    private final GptService gptService;
    private final DialogHistoryRepository dialogHistoryRepository;

    private final ChatLoggerHandler chatLoggerHandler;
    private final UserInformationHandler userInformationHandler;

    private boolean isRun;
    @Getter
    private Long dialogId;
    @Getter
    @Setter
    private Long memberId;
    @Getter
    private List<MultiChatMessage> currentHistory;
    private Long sequence;

    @PostConstruct
    public void init() {
        isRun = false;
        dialogId = 0L;
        memberId = 1L;
        sequence = 1L;
    }

    @TimerStart
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
    }

    public boolean hasDialog() {
        return isRun;
    }

    public int addMessage(List<MultiChatMessage> chatMessages) {
        if (!isRun) return -1;
        this.currentHistory.addAll(chatMessages);
        chatLoggerHandler.sendLog(currentHistory);
        return Math.toIntExact(sequence++);
    }

    public int addMessage(MultiChatMessage chatMessages) {
        if (!isRun) return -1;
        this.currentHistory.add(chatMessages);
        chatLoggerHandler.sendLog(currentHistory);
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
        UserInformationGenerateDto information = gptService.generateUserInformation(currentHistory);
        memberService.saveInformation(information, memberId);
    }

    public void sendUserInfo() {
        UserInformationResponseDto info = memberService.getInformation(memberId);
        userInformationHandler.sendInfo(info);
    }

    private Long generateDialogId() {
        return ++dialogId;
    }

    public boolean isValidDialogId(Long dialogId) {
        return this.dialogId.equals(dialogId);
    }
}
