package com.konkuk.gp.core.message.dto.client;

import com.konkuk.gp.core.gpt.enums.ChatType;

public class ClientEmergencyAlarmDto extends ClientResponseDto {
    public ClientEmergencyAlarmDto(String script) {
        super(
                script,
                ChatType.EMERGENCY.getName(),
                false,
                TriggerType.BY_EMERGENCY,
                -1L
        );
    }

    public static ClientEmergencyAlarmDto of(String data) {
        return new ClientEmergencyAlarmDto(data);
    }
}
