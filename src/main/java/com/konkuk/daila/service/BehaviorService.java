package com.konkuk.daila.service;

import com.konkuk.daila.domain.dao.Todolist;
import com.konkuk.daila.domain.dto.response.EmergencyCheckDto;
import com.konkuk.daila.domain.dto.response.TodoListResponseDto;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BehaviorService {

    private final PromptManager promptManager;
    private final GptService gptService;
    private final TodolistService todolistService;

    public EmergencyCheckDto checkEmergency(String caption) {
        Prompt checkEmergencyPrompt = promptManager.getCheckEmergencyPrompt();

        ChatCompletionRequest request = gptService.request()
                .addSystemMessage(checkEmergencyPrompt.getScript())
                .addUserMessage(String.format("caption : %s", caption))
                .topP(checkEmergencyPrompt.getTopP())
                .temperature(checkEmergencyPrompt.getTemperature())
                .build();

        return gptService.ask(request, EmergencyCheckDto.class);
    }

    public List<String> checkTodoByCaption(String caption, Long memberId) {

        List<Todolist> todoList = todolistService.findAllByMemberId(memberId);
        String script = generateCheckTodoScript(caption, todoList);

        Prompt checkTodoListPrompt = promptManager.getCheckTodoListPrompt();
        ChatCompletionRequest request = gptService.request()
                .addSystemMessage(checkTodoListPrompt.getScript())
                .addUserMessage(script)
                .topP(checkTodoListPrompt.getTopP())
                .temperature(checkTodoListPrompt.getTemperature())
                .build();

        TodoListResponseDto response = gptService.ask(request, TodoListResponseDto.class);
        return response.complete().stream()
                .map(todolistService::completeTodolist)
                .toList();
    }

    protected String generateCheckTodoScript(String caption, List<Todolist> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("input : ")
                .append(caption)
                .append(",")
                .append("checklist : [");

        for (Todolist checklist : list) {
            sb.append(checklist.toString()).append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
