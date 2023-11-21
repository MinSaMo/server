package com.konkuk.daila.global.logger.message.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatLog {

    public static String SENDER_USER = "user";
    public static String SENDER_DAILA = "diala";

    private String sender;
    private String script;
    private LocalDateTime timestamp;

    public ChatLog(String sender, String script) {
        this.sender = sender;
        this.script = script;
        timestamp = LocalDateTime.now();
    }
}
