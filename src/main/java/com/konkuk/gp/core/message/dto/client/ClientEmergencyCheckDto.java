package com.konkuk.gp.core.message.dto.client;

import com.konkuk.gp.core.gpt.enums.ChatType;

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
