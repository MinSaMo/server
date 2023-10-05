package com.konkuk.gp.config;

import com.konkuk.gp.core.socket.handler.dashboard.CaptionLoggerHandler;
import com.konkuk.gp.core.socket.handler.dashboard.ChatLoggerHandler;
import com.konkuk.gp.core.socket.handler.TextMessageHandler;
import com.konkuk.gp.core.socket.handler.dashboard.UserInformationHandler;
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
    private final ChatLoggerHandler chatLoggerHandler;
    private final UserInformationHandler userInformationHandler;
    private final CaptionLoggerHandler captionLoggerHandler;
    private final String SOCKET_URL_CLIENT = "/client";
    private final String SOCKET_URL_AI = "/ai";
    private final String SOCKET_URL_CHAT = "/log";
    private final String SOCKET_URL_INFO = "/info";
    private final String SOCKET_URL_CAPTION = "/caption";

    @Autowired
    public WebSocketConfiguration(
            @Qualifier("client") TextMessageHandler clientSocketHandler,
            @Qualifier("ai") TextMessageHandler aiSocketHandler, ChatLoggerHandler chatLoggerHandler, UserInformationHandler userInformationHandler, CaptionLoggerHandler captionLoggerHandler) {
        this.clientSocketHandler = clientSocketHandler;
        this.aiSocketHandler = aiSocketHandler;
        this.chatLoggerHandler = chatLoggerHandler;
        this.userInformationHandler = userInformationHandler;
        this.captionLoggerHandler = captionLoggerHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(clientSocketHandler, SOCKET_URL_CLIENT)
                .addHandler(aiSocketHandler, SOCKET_URL_AI)
                .addHandler(chatLoggerHandler, SOCKET_URL_CHAT)
                .addHandler(userInformationHandler, SOCKET_URL_INFO)
                .addHandler(captionLoggerHandler, SOCKET_URL_CAPTION)
                .setAllowedOrigins("*");
    }

}
