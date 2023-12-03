package com.konkuk.daila.global.logger;

import com.konkuk.daila.controller.stomp.dto.client.ClientResponseDto;
import com.konkuk.daila.global.logger.message.user.ChatLog;
import com.konkuk.daila.service.enums.ChatType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DashboardLogger {

    private final SimpMessagingTemplate template;
    private final ObjectProvider<ClientLogProperty> clientLogPropertyProvider;
    private final ObjectProvider<AiLogProperty> aiLogPropertyProvider;
    private final UserInformationLogProperty userInformationLogProperty;

    private ClientLogProperty clientLogProperty;
    private AiLogProperty aiLogProperty;

    public void sendUserInformationLog() {
        userInformationLogProperty.load();
        template.convertAndSend(TopicType.LOG_DB_USERINFO.getPath(), userInformationLogProperty.getUserInformationLog());
    }

    public void sendCaptionLog(String caption, Long memberId) {
        aiLogProperty = aiLogPropertyProvider.getObject();
        aiLogProperty.setCaption(caption);
        aiLogProperty.setMemberId(memberId);
        aiLogProperty.setUuid(UUID.randomUUID());

        template.convertAndSend(TopicType.LOG_AI_CAPTION.getPath(), aiLogProperty.getCaptionLogMessage());
//        template.convertAndSend(TopicType.SERVICE_REPLY.getPath(),
//                ClientResponseDto.ofBehavior(caption));
    }

    public void sendEmergencyCheckLog() {
        if (aiLogProperty != null) {
            aiLogProperty.setEmergencyCheckTimestamp(LocalDateTime.now());

            template.convertAndSend(TopicType.LOG_AI_EMERGENCY_CHECK.getPath(), aiLogProperty.getEmergencyCheckLogMessage());
        }
    }

    public void sendEmergencyOccurLog() {
        if (aiLogProperty != null) {
            aiLogProperty.setEmergencyOccurTimestamp(LocalDateTime.now());

            template.convertAndSend(TopicType.LOG_AI_EMERGENCY_OCCUR.getPath(), aiLogProperty.getEmergencyOccurLogMessage());
        }
    }

    public void sendTodoCompleteLog(List<String> todo, String caption) {
        template.convertAndSend(TopicType.LOG_DB_USERINFO.getPath(), userInformationLogProperty.getUserInformationLog());
        if (aiLogProperty != null) {
            aiLogProperty.setCompletedTodolist(todo);

            template.convertAndSend(TopicType.LOG_AI_COMPLETE_TODO.getPath(), aiLogProperty.getTodoCompleteLogMessage());
        }
    }

    public void sendCaptionReplyLog(String reply) {
        if (aiLogProperty != null) {
            aiLogProperty.setReply(reply);

            template.convertAndSend(TopicType.LOG_AI_REPLY.getPath(), aiLogProperty.getReplyLogMessage());
        }
    }

    public void sendScriptLog(String script, Long memberId, Long dialogId) {
        clientLogProperty = clientLogPropertyProvider.getObject();
        clientLogProperty.setScript(script);
        clientLogProperty.setMemberId(memberId);
        clientLogProperty.setDialogId(dialogId);
        clientLogProperty.setUuid(UUID.randomUUID());
        template.convertAndSend(TopicType.LOG_CLIENT_SCRIPT.getPath(), clientLogProperty.getScriptLogMessage());
        template.convertAndSend(TopicType.LOG_CHAT.getPath(), new ChatLog(ChatLog.SENDER_USER, script));
        template.convertAndSend(TopicType.SERVICE_REPLY.getPath(),
                ClientResponseDto.ofUser(script)
        );
    }

    public void sendScriptLogForGpt(String script) {
        template.convertAndSend("/topic/service/gpt",
                ClientResponseDto.ofUser(script));
    }

    public void sendIntenseLog(ChatType intense) {
        if (clientLogProperty != null) {
            clientLogProperty.setIntense(intense);
            template.convertAndSend(TopicType.LOG_CLIENT_INTENSE.getPath(), clientLogProperty.getIntenseLogMessage());
        }
    }

    public void sendPromptLog(String prompt) {
        if (clientLogProperty != null) {
            clientLogProperty.setPrompt(prompt);
            template.convertAndSend(TopicType.LOG_CLIENT_PROMPT.getPath(), clientLogProperty.getPromptLogMessage());
        }
    }

    public void sendReplyLog(String reply) {
        clientLogProperty.setReply(reply);
        template.convertAndSend(TopicType.LOG_CLIENT_REPLY.getPath(), clientLogProperty.getReplyLogMessage());
        template.convertAndSend(TopicType.LOG_CHAT.getPath(),new ChatLog(ChatLog.SENDER_DAILA,reply));
    }

}
