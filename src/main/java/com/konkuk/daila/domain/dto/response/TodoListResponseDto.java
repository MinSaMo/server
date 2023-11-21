package com.konkuk.daila.domain.dto.response;

import java.util.List;

public record TodoListResponseDto(
        List<Long> complete
) {
}
