package com.konkuk.daila.domain.dto.response;

import lombok.Getter;

@Getter
public class ResponseClassifyDto {
    private int type;

    public boolean isYes() {
        return type > 0;
    }

    public boolean isNo() {
        return type < 0;
    }
}
