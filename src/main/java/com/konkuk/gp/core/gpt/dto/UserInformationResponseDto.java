package com.konkuk.gp.core.gpt.dto;

import com.konkuk.gp.domain.dto.request.ChecklistCreateDto;

import java.util.List;

public record UserInformationResponseDto(
        List<String> diseases,
        List<ChecklistCreateDto> todoList,
        List<String> preferredFoods
) {
}
