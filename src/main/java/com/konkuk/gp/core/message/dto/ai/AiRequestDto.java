package com.konkuk.gp.core.message.dto.ai;

import lombok.Data;

@Data
public class AiRequestDto {
    private String caption;
    private Boolean isReal;
}
