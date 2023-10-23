package com.konkuk.gp.controller.stomp.dto.client;

import com.konkuk.gp.service.enums.ChatType;

public class ClientEmergencyCheckDto extends ClientResponseDto {
    private ClientEmergencyCheckDto(String script) {
        super(
                script,
                ChatType.EMERGENCY.getName(),
                false,
                TriggerType.BY_EMERGENCY,
                -1L
        );
    }

    public static ClientEmergencyCheckDto of(String data) {
        return new ClientEmergencyCheckDto(data);
    }

}
