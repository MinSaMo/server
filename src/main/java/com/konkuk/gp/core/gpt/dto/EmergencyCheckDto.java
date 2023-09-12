package com.konkuk.gp.core.gpt.dto;

public record EmergencyCheckDto(
        boolean isDetected,
        String reason) {
}
