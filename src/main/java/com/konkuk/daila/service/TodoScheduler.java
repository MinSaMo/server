package com.konkuk.daila.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class TodoScheduler {
    private final ThreadPoolTaskScheduler taskScheduler;

    public TodoScheduler() {
        this.taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();
    }

    public void schedule(Runnable runnable, LocalDateTime time) {
        taskScheduler.schedule(runnable, triggerContext -> {
            CronTrigger cronTrigger = new CronTrigger(generateCronExpression(time));
            return cronTrigger.nextExecutionTime(triggerContext);
        });
    }

    private String generateCronExpression(LocalDateTime time) {
        int hour = time.getHour();
        int minute = time.getMinute();
        return String.format("0 %d %d ? * *", minute, hour);
    }
}
