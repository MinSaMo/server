package com.konkuk.gp.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TodolistCreateDto(
        String description,
        @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime deadline
) {
}
