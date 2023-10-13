package com.konkuk.gp.core.socket.aop;

import com.konkuk.gp.core.socket.DialogManager;
import com.konkuk.gp.core.socket.runner.DialogTimer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DialogTimerAdvisor {

    private final DialogTimer timer;
    private final DialogManager manager;

    @After("@annotation(com.konkuk.gp.core.socket.aop.TimerStart)")
    public void timerStart(JoinPoint joinPoint) {
        timer.start();
    }

    @After("@annotation(com.konkuk.gp.core.socket.aop.TimerEnd)")
    public void timerEnd(JoinPoint joinPoint) {
        log.info("[AOP] TIMER END");
        manager.endDialog();
    }
}
