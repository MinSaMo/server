package com.konkuk.daila.global.thread;

import com.konkuk.daila.global.logger.DashboardLogger;
import com.konkuk.daila.global.logger.UserInformationLogProperty;
import com.konkuk.daila.service.BehaviorService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class TodolistCheckRunner implements Runnable {

    private final BehaviorService behaviorService;
    private final DashboardLogger logger;
    private final UserInformationLogProperty userInformationLogProperty;

    private String caption;
    private Long memberId;
    @Override
    public void run() {
        List<String> todos = behaviorService.checkTodoByCaption(caption, memberId);
        for (String description : todos) {
            userInformationLogProperty.completeTodo(description, caption);
        }
        logger.sendTodoCompleteLog(todos,caption);
        logger.sendUserInformationLog();
    }

    public void setParam(String caption, Long memberId) {
        this.caption = caption;
        this.memberId = memberId;
    }
}
