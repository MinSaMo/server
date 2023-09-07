package com.konkuk.gp.config;

import io.github.flashvayne.chatgpt.property.ChatgptProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatgptConfiguration {

    private final ChatgptProperties chatgptProperties;
}
