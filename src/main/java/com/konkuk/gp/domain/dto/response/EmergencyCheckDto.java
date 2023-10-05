package com.konkuk.gp.domain.dto.response;

public record EmergencyCheckDto(
        boolean isDetected,
        String reason) {
}
