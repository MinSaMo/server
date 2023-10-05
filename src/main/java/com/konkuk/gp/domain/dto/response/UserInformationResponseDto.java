package com.konkuk.gp.domain.dto.response;

import com.konkuk.gp.domain.dao.Checklist;

import java.util.List;

public record UserInformationResponseDto(
        List<String> diseases,
        List<Checklist> todoList,
        List<String> preferredFoods
) {
}
