package com.konkuk.gp.domain.dto.response;

import java.util.List;

public record TodoListResponseDto(
        List<Long> complete
) {
}
