package com.konkuk.gp.service;

import com.konkuk.gp.service.dialog.TimerEnd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@Component
@RequiredArgsConstructor
public class DialogTimerService {

    private final int timeOut = 10 * 1000;
    private Timer timer = null;
    private boolean isRun;

    @PostConstruct
    void init() {
        isRun = false;
    }

    public void start() {
        if (timer == null) timer = new Timer();
        reset();
        log.info("[Timer] Timer start");
    }

    public void reset() {
        if (isRun) {
            timer.cancel();
            timer.purge();
            timer = new Timer();
        }
        timer.schedule(callEndDialog(), timeOut);
        isRun = true;
    }

    public TimerTask callEndDialog() {
        return new TimerTask() {
            @Override
            public void run() {
                endDialog();
            }
        };
    }

    @TimerEnd
    public void endDialog() {
        log.info("[Timer] Timer End");
        timer = null;
        isRun = false;
    }
}
