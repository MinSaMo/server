package com.konkuk.daila.controller.stomp.dto.client;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ClientEmergencyCheckDto {
    private String reason;
    private int timeout;
    private LocalDateTime timestamp;

    @Builder
    public ClientEmergencyCheckDto(String reason, int timeout) {
        this.reason = reason;
        this.timeout = timeout;
        timestamp = LocalDateTime.now();
    }
}
