package com.konkuk.gp.core.gpt.dto;

import java.util.List;

public record TodoListResponseDto(
        List<Long> complete
) {

}
