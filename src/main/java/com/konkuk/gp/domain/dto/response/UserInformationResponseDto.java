package com.konkuk.gp.domain.dto.response;

import com.konkuk.gp.domain.dao.Todolist;

import java.util.List;

public record UserInformationResponseDto(
        List<String> diseases,
        List<Todolist> todoList,
        List<String> preferredFoods
) {
}
