package com.konkuk.gp.core.socket.handler.dashboard;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CaptionDto(
        String caption,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime timestamp
) {
}
