package com.konkuk.gp.core.gpt.enums;

import lombok.Getter;

public enum ChatType {
    ADVICE(1, "ADVICE"),
    DAILY(2, "DAILY"),
    EMERGENCY(3, "EMERGENCY");

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
