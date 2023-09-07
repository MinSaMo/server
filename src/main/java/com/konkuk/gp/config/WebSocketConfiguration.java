package com.konkuk.gp.config;

import com.konkuk.gp.core.socket.handler.TextMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final TextMessageHandler clientSocketHandler;
    private final TextMessageHandler aiSocketHandler;
    private final String SOCKET_URL_CLIENT = "/client";
    private final String SOCKET_URL_AI = "/ai";

    @Autowired
    public WebSocketConfiguration(
            @Qualifier("client") TextMessageHandler clientSocketHandler,
            @Qualifier("ai") TextMessageHandler aiSocketHandler) {
        this.clientSocketHandler = clientSocketHandler;
        this.aiSocketHandler = aiSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(clientSocketHandler, SOCKET_URL_CLIENT)
                .addHandler(aiSocketHandler, SOCKET_URL_AI)
                .setAllowedOrigins("*");
    }

}
