package com.konkuk.gp.controller.stomp.dto.ai;

import lombok.Data;

@Data
public class AiRequestDto {
    private String caption;
    private Boolean isReal;
}
