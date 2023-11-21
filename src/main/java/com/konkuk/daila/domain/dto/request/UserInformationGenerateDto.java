package com.konkuk.daila.domain.dto.request;

import java.util.List;

public record UserInformationGenerateDto(

        List<String> diseases,
        List<TodolistCreateDto> todoList,
        List<String> preferredFoods

) {
}
