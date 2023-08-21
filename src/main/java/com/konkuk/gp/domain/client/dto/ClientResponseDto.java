package com.konkuk.gp.domain.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ClientResponseDto {
    private String script;
    // TODO : enum
    private String type;
    private boolean isFinish;
    private int situation;
    private Long dialogId;

    @Builder
    public ClientResponseDto(String script, String type, boolean isFinish, TriggerType triggerType, Long dialogId) {
        this.script = script;
        this.type = type;
        this.isFinish = isFinish;
        this.situation = triggerType.getVal();
        this.dialogId = dialogId;
    }
}
