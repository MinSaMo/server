package com.konkuk.gp.config;

import com.konkuk.gp.ai.AiSocketHandler;
import com.konkuk.gp.client.ClientSocketHandler;
import com.konkuk.gp.core.socket.SessionRegistry;
import com.konkuk.gp.core.socket.SessionType;
import com.konkuk.gp.core.socket.TextMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(clientSocketHandler(sessionRegistry()), "/client")
                .addHandler(aiSocketHandler(sessionRegistry()), "/ai")
                .setAllowedOrigins("*");
    }

    public TextMessageHandler clientSocketHandler(SessionRegistry sessionRegistry) {
        return new ClientSocketHandler(sessionRegistry,SessionType.CLIENT);
    }

    public TextMessageHandler aiSocketHandler(SessionRegistry sessionRegistry) {
        return new AiSocketHandler(sessionRegistry, SessionType.AI);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistry();
    }
}
