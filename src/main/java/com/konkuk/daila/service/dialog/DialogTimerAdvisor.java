package com.konkuk.daila.service.dialog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DialogTimerAdvisor {

    private final DialogTimerService timer;
    private final DialogService manager;

    @After("@annotation(com.konkuk.daila.service.dialog.TimerStart)")
    public void timerStart(JoinPoint joinPoint) {
        timer.start();
    }

    @Before("@annotation(com.konkuk.daila.service.dialog.TimerStart)")
    public void timerStartBeforeChat(JoinPoint joinPoint) {
        timer.stop();
    }

    @After("@annotation(com.konkuk.daila.service.dialog.TimerEnd)")
    public void timerEnd(JoinPoint joinPoint) {
        log.info("[AOP] TIMER END");
        manager.endDialog();
    }
}
