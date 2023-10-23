package com.konkuk.gp.controller.stomp.dto.client;

import com.konkuk.gp.service.enums.ChatType;

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
