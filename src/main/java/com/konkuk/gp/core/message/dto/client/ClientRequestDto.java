package com.konkuk.gp.core.message.dto.client;

import lombok.*;

@Data
public class ClientRequestDto {
    private String script;
    private Long dialogId;
    private Boolean isReal;
}
