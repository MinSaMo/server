package com.konkuk.gp.core.gpt;

import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatGptService {

    private final ChatgptService chatgptService;

    public String simpleChat(String msg) {
        return chatgptService.sendMessage(msg);
    }
}
