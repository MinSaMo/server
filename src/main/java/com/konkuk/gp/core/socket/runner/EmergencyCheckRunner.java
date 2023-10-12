package com.konkuk.gp.core.socket.runner;

import com.konkuk.gp.core.message.Message;
import com.konkuk.gp.core.socket.SessionRegistry;
import com.konkuk.gp.core.socket.SessionType;
import com.konkuk.gp.core.socket.handler.ClientSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Timer;
import java.util.TimerTask;

@Scope("prototype")
@Component
@RequiredArgsConstructor
public class EmergencyCheckRunner implements Runnable {

    private final ClientSocketHandler handler;
    private final SessionRegistry sessionRegistry;

    private final int tiemOut = 5000;

    @Override
    public void run() {
        Timer timer = new Timer();
        WebSocketSession session = sessionRegistry.getSession(SessionType.CLIENT)
                .orElseThrow(() -> new RuntimeException("Session not found : client"));
        handler.sendMessage(session, Message.of("Please Check Emergency in " + tiemOut + "ms", Message.STATUS_OK));
        timer.schedule(sendEmergencyAlarm(session), tiemOut);
    }

    public TimerTask sendEmergencyAlarm(WebSocketSession session) {
        return new TimerTask() {
            @Override
            public void run() {
                handler.sendMessage(session, Message.of("Emergency Occur", Message.STATUS_OK));
            }
        };
    }


}
