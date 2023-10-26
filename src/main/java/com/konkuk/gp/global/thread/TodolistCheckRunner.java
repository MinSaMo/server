package com.konkuk.gp.global.thread;

import com.konkuk.gp.global.logger.DashboardLogger;
import com.konkuk.gp.service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class TodolistCheckRunner implements Runnable {

    private final GptService gptService;
    private final DashboardLogger logger;
    private String caption;
    private Long memberId;
    @Override
    public void run() {
        List<String> todos = gptService.checkCompletedTodolist(caption, memberId);
        logger.sendTodoCompleteLog(todos);
    }

    public void setParam(String caption, Long memberId) {
        this.caption = caption;
        this.memberId = memberId;
    }
}
