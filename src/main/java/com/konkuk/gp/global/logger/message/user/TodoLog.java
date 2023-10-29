package com.konkuk.gp.global.logger.message.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class TodoLog {
    private String name;
    private LocalDateTime deadline;
    @Setter
    @Builder.Default
    private boolean isFinish = false;
    @Setter
    @Builder.Default
    private String finishReason = "";
}
