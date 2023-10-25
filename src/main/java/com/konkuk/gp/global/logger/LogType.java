package com.konkuk.gp.global.logger;

import lombok.Getter;

@Getter
public enum LogType {
    SCRIPT("/sub/script"),
    INTENSE("/sub/intense"),
    PROMPT("/sub/prompt"),
    REPLY("/sub/reply"),
    EMERGENCY("/sub/emergency")
    ;

    private final String path;

    LogType(String path) {
        this.path = path;
    }

}
