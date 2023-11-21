package com.konkuk.daila.global.thread;

import com.konkuk.daila.global.logger.DashboardLogger;
import com.konkuk.daila.global.logger.UserInformationLogProperty;
import com.konkuk.daila.service.GptService;
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
    private final UserInformationLogProperty userInformationLogProperty;

    private String caption;
    private Long memberId;
    @Override
    public void run() {
        List<String> todos = gptService.checkCompletedTodolist(caption, memberId);
        for (String todo : todos) {
            userInformationLogProperty.completeTodo(todo, caption);
        }
        logger.sendTodoCompleteLog(todos,caption);
        logger.sendUserInformationLog();
    }

    public void setParam(String caption, Long memberId) {
        this.caption = caption;
        this.memberId = memberId;
    }
}
