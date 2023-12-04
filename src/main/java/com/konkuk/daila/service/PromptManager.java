package com.konkuk.daila.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Getter
public class PromptManager {

    private final Prompt intensePrompt;
    private final Prompt globalPrompt;
    private final Prompt dailyPrompt;
    private final Prompt infoPrompt;
    private final Prompt checkTodoListPrompt;
    private final Prompt checkEmergencyPrompt;
    private final Prompt normalLLMPrompt;
    private final Prompt duplicatePrompt;
    private final Prompt checkResponseToCaptionPrompt;
    private final Prompt classifyPrompt;
    private final Prompt classifyUserMessagePrompt;
    private final Prompt todoAlarmPrompt;


    public PromptManager(
            @Value("${gpt.script.intense}") String intenseScript,
            @Value("${gpt.script.global}") String globalScript,
            @Value("${gpt.script.daily}") String dailyScript,
            @Value("${gpt.script.information}") String infoScript,
            @Value("${gpt.script.check-todolist}") String todoScript,
            @Value("${gpt.script.check-emergency}") String emergencyScript,
            @Value("${gpt.script.llm}") String llmScript,
            @Value("${gpt.script.duplicate}") String duplicate,
            @Value("${gpt.script.check-response}") String checkResponse,
            @Value("${gpt.script.classify}") String classify,
            @Value("${gpt.script.classify_users}") String yesOrNo,
            @Value("${gpt.script.todo-script}") String todoAlarm
    ) {
        intensePrompt = Prompt.builder()
                .script(intenseScript)
                .topP(0.3)
                .temperature(0.1)
                .build();
        globalPrompt = Prompt.builder()
                .script(globalScript)
                .build();
        dailyPrompt = Prompt.builder()
                .script(dailyScript)
                .topP(0.3)
                .temperature(0.4)
                .build();
        infoPrompt = Prompt.builder()
                .script(infoScript)
                .topP(0)
                .temperature(0.2)
                .build();
        checkTodoListPrompt = Prompt.builder()
                .script(todoScript)
                .topP(0.3)
                .temperature(0.1)
                .build();
        checkEmergencyPrompt = Prompt.builder()
                .script(emergencyScript)
                .topP(0.3)
                .temperature(0.1)
                .build();
        normalLLMPrompt = Prompt.builder()
                .script(llmScript)
                .topP(0.3)
                .temperature(0.4)
                .build();
        duplicatePrompt = Prompt.builder()
                .script(duplicate)
                .topP(0.2)
                .temperature(0.1)
                .build();
        checkResponseToCaptionPrompt = Prompt.builder()
                .script(checkResponse)
                .topP(0.3)
                .temperature(0.4)
                .build();
        classifyPrompt = Prompt.builder()
                .script(classify)
                .topP(0.1)
                .temperature(0.2)
                .build();
        classifyUserMessagePrompt = Prompt.builder()
                .script(yesOrNo)
                .topP(0.1)
                .temperature(0.2)
                .build();
        todoAlarmPrompt = Prompt.builder()
                .script(todoAlarm)
                .topP(0.3)
                .temperature(0.3)
                .build();
    }

    public String setPromptParams(String template, Map<String, String> params) {
        String res = template;
        for (String name : params.keySet()) {
            res = res.replace(name, params.get(name));
        }
        return res;
    }

}
