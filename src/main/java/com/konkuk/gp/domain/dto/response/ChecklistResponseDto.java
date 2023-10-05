package com.konkuk.gp.domain.dto.response;

import com.konkuk.gp.domain.dto.request.ChecklistCreateDto;

import java.util.List;

public record ChecklistResponseDto(
        List<String> diseases,
        List<ChecklistCreateDto> todoList,
        List<String> preferredFoods
) {

}
