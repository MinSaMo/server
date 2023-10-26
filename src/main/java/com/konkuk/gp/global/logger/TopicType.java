package com.konkuk.gp.global.logger;

import lombok.Getter;

@Getter
public enum TopicType {
    // For Service
    SERVICE_REPLY("/topic/service/reply"),
    SERVICE_EMERGENCY("/topic/service/emerge"),

    // For Client Log
    LOG_CLIENT_SCRIPT("/topic/log/client/script"),
    LOG_CLIENT_INTENSE("/topic/log/client/intense"),
    LOG_CLIENT_PROMPT("/topic/log/client/prompt"),
    LOG_CLIENT_REPLY("/topic/log/client/reply"),

    // For AI Log
    LOG_AI_CAPTION("/topic/log/ai/caption"),
    LOG_AI_EMERGENCY("/topic/log/ai/emerge"),
    LOG_AI_EMERGENCY_CHECK("/topic/log/ai/emerge_check"),
    LOG_AI_EMERGENCY_OCCUR("/topic/log/ai/emerge_occur"),
    LOG_AI_COMPLETE_TODO("/topic/log/ai/complete_todo"),
    LOG_AI_REPLY("/topic/log/ai/reply")
    ;

    private final String path;

    TopicType(String path) {
        this.path = path;
    }

}
