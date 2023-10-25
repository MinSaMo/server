package com.konkuk.gp.global.thread;

import com.konkuk.gp.controller.stomp.dto.client.ClientEmergencyCheckDto;
import com.konkuk.gp.controller.stomp.dto.client.ClientResponseDto;
import com.konkuk.gp.controller.stomp.dto.client.TriggerType;
import com.konkuk.gp.global.logger.LogType;
import com.konkuk.gp.service.enums.ChatType;
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
        template.convertAndSend(LogType.EMERGENCY.getPath(), dto);
    }

    public void sendCheckMessageToClient() {
        ClientResponseDto dto = ClientResponseDto.builder()
                .script("응급상황인가요?")
                .time(0L)
                .triggerType(TriggerType.BY_EMERGENCY)
                .type(ChatType.EMERGENCY.getName())
                .dialogId(-1L)
                .build();

        template.convertAndSend(LogType.REPLY.getPath(), dto);
    }

    private void sendAlarmMessage() {
        ClientResponseDto dto = ClientResponseDto.builder()
                .script("응급상황 조치 실행")
                .time(0L)
                .triggerType(TriggerType.BY_EMERGENCY)
                .type(ChatType.EMERGENCY.getName())
                .dialogId(-1L)
                .build();
        template.convertAndSend(LogType.REPLY.getPath(), dto);
    }
}
