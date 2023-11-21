package com.konkuk.daila.service.enums;

import lombok.Getter;

public enum ChatType {
    LLM(0, "LLM"),
    ADVICE(1, "ADVICE"),
    DAILY(2, "DAILY"),
    EMERGENCY(3, "EMERGENCY"),
    SERVER_ERR(-1, "SERVER_ERR"),;

    private final int val;
    @Getter
    private final String name;

    ChatType(int val, String name) {
        this.val = val;
        this.name = name;
    }

    public static ChatType of(int i) {
        for (ChatType type : ChatType.values()) {
            if (type.val == i) return type;
        }
        throw new RuntimeException("Not found : chat type of " + i);
    }
}
