package com.konkuk.gp.global.logger;

import com.konkuk.gp.service.enums.ChatType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DashboardLogger {

    private final SimpMessagingTemplate template;
    private final ObjectProvider<LogProperty> logPropertyProvider;

    private LogProperty logProperty;

    public void sendScriptLog(String script, Long memberId, Long dialogId) {
        logProperty = logPropertyProvider.getObject();
        logProperty.setScript(script);
        logProperty.setMemberId(memberId);
        logProperty.setDialogId(dialogId);
        logProperty.setUuid(UUID.randomUUID());
        template.convertAndSend(LogType.SCRIPT.getPath(), logProperty.getScriptLogMessage());
    }

    public void sendIntenseLog(ChatType intense) {
        logProperty.setIntense(intense);
        template.convertAndSend(LogType.INTENSE.getPath(), logProperty.getIntenseLogMessage());
    }

    public void sendPromptLog(String prompt) {
        logProperty.setPrompt(prompt);
        template.convertAndSend(LogType.PROMPT.getPath(), logProperty.getPromptLogMessage());
    }

    public void sendReplyLog(String reply) {
        logProperty.setReply(reply);
        template.convertAndSend(LogType.REPLY.getPath(), logProperty.getReplyLogMessage());
    }

}
