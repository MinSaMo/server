package com.konkuk.gp.global.exception.socket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorData {
    private ErrorCode code;
    private String errMsg;
}
