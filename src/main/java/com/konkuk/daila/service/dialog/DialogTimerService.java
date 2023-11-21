package com.konkuk.daila.service.dialog;

import com.konkuk.daila.service.dialog.DialogService;
import com.konkuk.daila.service.dialog.TimerEnd;
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

    private final DialogService dialogManager;
    private final int timeOut = 30 * 1000;
    private Timer timer = null;
    private boolean isRun;

    @PostConstruct
    void init() {
        isRun = false;
    }

    public void start() {
        if (timer == null) timer = new Timer();
        reset();
        isRun = true;
        log.info("[Timer] Timer start");
    }

    public void stop() {
        if(isRun) {
            timer.cancel();
            timer.purge();
            timer = new Timer();
            isRun = false;
        }
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
        dialogManager.endDialog();
        timer = null;
        isRun = false;
    }
}
