package com.konkuk.daila.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konkuk.daila.global.logger.DashboardLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageHeaders;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class WebSocketSubscribeListener implements ApplicationListener<SessionSubscribeEvent> {

    private final ObjectMapper objectMapper;
    private final DashboardLogger logger;

    private final String INFO = "/topic/information";

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        Map<String, Object> nativeHeaders = objectMapper.convertValue(headers.get("nativeHeaders"),
                new TypeReference<>() {
                });
        Object dstObj = nativeHeaders.get("destination");
        List<String> destinations = objectMapper.convertValue(dstObj,
                new TypeReference<>() {
                });
        for (String destination : destinations) {
            if (destination.equals(INFO)) {
                sendInfoLog();
            }
        }
    }

    public void sendInfoLog() {
        logger.sendUserInformationLog();
    }
}
