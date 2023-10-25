package com.konkuk.gp.global.thread;

import com.konkuk.gp.service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class TodolistCheckRunner implements Runnable {

    private final GptService gptService;
    private String caption;
    private Long memberId;
    @Override
    public void run() {
        gptService.checkCompletedTodolist(caption, memberId);
    }

    public void setParam(String caption, Long memberId) {
        this.caption = caption;
        this.memberId = memberId;
    }
}
