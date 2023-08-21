package com.konkuk.gp.core.gpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konkuk.gp.core.gpt.dto.IntenseResponseDto;
import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGptService {

    private final ChatgptService chatgptService;
    private final ObjectMapper objectMapper;

    public String simpleChat(String msg) {
        return chatgptService.sendMessage(msg);
    }

    @Value("${script.system.intense}")
    private String intenseSystemScript;

    public int determineIntense(String script) {
        MultiChatMessage systemDefinition = getSystemDefinition();
        List<MultiChatMessage> messages = Arrays.asList(
                systemDefinition,
                new MultiChatMessage("user", script));
        String response = chatgptService.multiChat(messages);
        try {
            IntenseResponseDto result = objectMapper.readValue(response, IntenseResponseDto.class);
            return result.answerTypeIndex();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected void responseWithDailyChat(String script) {
        // daily chat
        // answer with daily mood
    }

    protected void responseWithAdvice(String script) {
        // advice chat
        // answer with advice
    }

    protected MultiChatMessage getSystemDefinition() {
        return new MultiChatMessage("system", intenseSystemScript);
    }
}
