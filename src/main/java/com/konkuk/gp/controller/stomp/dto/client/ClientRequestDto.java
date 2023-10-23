package com.konkuk.gp.controller.stomp.dto.client;

import lombok.*;

@Data
public class ClientRequestDto {
    private String script;
    private Long dialogId;
    private Boolean isReal;
}
