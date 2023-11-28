package com.konkuk.daila.global.thread;

import com.konkuk.daila.controller.stomp.dto.client.ClientEmergencyCheckDto;
import com.konkuk.daila.controller.stomp.dto.client.ClientResponseDto;
import com.konkuk.daila.global.logger.DashboardLogger;
import com.konkuk.daila.global.logger.TopicType;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

@Scope("prototype")
@Component
@RequiredArgsConstructor
public class EmergencyCheckRunner implements Runnable {

    private final SimpMessagingTemplate template;
    private final DashboardLogger logger;
    private final int timeOut = 10000;

    @Setter
    private String caption;

    @Override
    public void run() {
        assert caption != null;
        Timer timer = new Timer();
        sendCheckMessage();
        timer.schedule(sendEmergencyAlarm(), timeOut);
    }

    public TimerTask sendEmergencyAlarm() {
        return new TimerTask() {
            @Override
            public void run() {
                sendAlarmMessage();
            }
        };
    }

    public void sendCheckMessage() {
        sendCheckMessageToEmergency();
        sendCheckMessageToClient();
    }

    public void sendCheckMessageToEmergency() {
        ClientEmergencyCheckDto dto = ClientEmergencyCheckDto.builder()
                .timeout(timeOut)
                .reason(caption)
                .build();
        template.convertAndSend(TopicType.LOG_AI_EMERGENCY.getPath(), dto);
    }

    public void sendCheckMessageToClient() {
        ClientResponseDto dto = ClientResponseDto.builder()
                .script("응급상황인가요?")
                .time(0L)
                .build();
        logger.sendEmergencyCheckLog();
        template.convertAndSend(TopicType.SERVICE_EMERGENCY.getPath(), dto);
    }

    private void sendAlarmMessage() {
        ClientResponseDto dto = ClientResponseDto.builder()
                .script("응급상황 조치 실행")
                .time(0L)
                .build();
        logger.sendEmergencyOccurLog();
        template.convertAndSend(TopicType.SERVICE_EMERGENCY.getPath(), dto);
    }
}
