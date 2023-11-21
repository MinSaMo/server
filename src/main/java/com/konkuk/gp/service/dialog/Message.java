package com.konkuk.gp.service.dialog;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Message {

    public static String SENDER_USER = "user";
    public static String SENDER_ASSISTANT = "assistant";
    private final String sender;
    private final String script;
    private final LocalDateTime timestamp;

    protected Message(String sender, String script) {
        this.sender = sender;
        this.script = script;
        timestamp = LocalDateTime.now();
    }

    public static Message ofUser(String script) {
        return new Message(SENDER_USER, script);
    }

    public static Message ofAssistant(String script) {
        return new Message(SENDER_ASSISTANT, script);
    }
}
