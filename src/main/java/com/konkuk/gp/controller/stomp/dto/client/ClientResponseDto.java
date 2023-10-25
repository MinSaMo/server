package com.konkuk.gp.controller.stomp.dto.client;

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
    private boolean isFinish;
    private int situation;
    private Long dialogId;
    private String responseTime;

    @Builder
    public ClientResponseDto(String script, String type, boolean isFinish, TriggerType triggerType, Long dialogId, Long time) {
        this.script = script;
        this.type = type;
        this.isFinish = isFinish;
        this.situation = triggerType.getVal();
        this.dialogId = dialogId;
        double seconds = (double) time / 1000.0;
        String formattedSeconds = String.format("%.2f", seconds);
        this.responseTime = formattedSeconds + "s";
    }
}
