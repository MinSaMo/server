package com.konkuk.daila.controller.stomp.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponseDto {
    private String script;
    // TODO : enum
    private String type;
    private Long dialogId;
    private String responseTime;

    @Builder
    public ClientResponseDto(String script, String type, Long dialogId, Long time) {
        this.script = script;
        this.type = type;
        this.dialogId = dialogId;
        double seconds = (double) time / 1000.0;
        String formattedSeconds = String.format("%.2f", seconds);
        this.responseTime = formattedSeconds + "s";
    }
}
