package com.konkuk.daila.controller.stomp.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponseDto {
    public final static String SENDER_USER = "user";
    public final static String SENDER_ASSISTANT = "assistant";

    private String script;
    private String sender;
    private String responseTime;
    private LocalDateTime timestamp;

    @Builder
    public ClientResponseDto(String script, Long time) {
        this.script = script;
        double seconds = (double) time / 1000.0;
        String formattedSeconds = String.format("%.2f", seconds);
        this.responseTime = formattedSeconds + "s";
        this.timestamp = LocalDateTime.now();
        this.sender = SENDER_ASSISTANT;
    }

    public static ClientResponseDto ofUser(String script, Long time) {
        ClientResponseDto response = ClientResponseDto.builder()
                .script(script)
                .time(time)
                .build();
        response.setSender(SENDER_USER);
        return response;
    }

}
