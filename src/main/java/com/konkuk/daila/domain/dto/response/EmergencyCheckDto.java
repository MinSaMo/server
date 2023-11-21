package com.konkuk.daila.domain.dto.response;

public record EmergencyCheckDto(
        boolean isDetected,
        String reason) {
}
