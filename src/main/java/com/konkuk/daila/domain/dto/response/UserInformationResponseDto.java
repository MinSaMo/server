package com.konkuk.daila.domain.dto.response;

import com.konkuk.daila.domain.dao.Todolist;

import java.util.List;

public record UserInformationResponseDto(
        List<String> diseases,
        List<Todolist> todoList,
        List<String> preferredFoods
) {
}
