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


    public PromptManager(
            @Value("${gpt.script.intense}") String intenseScript,
            @Value("${gpt.script.global}") String globalScript,
            @Value("${gpt.script.daily}") String dailyScript,
            @Value("${gpt.script.information}") String infoScript,
            @Value("${gpt.script.check-todolist}") String todoScript,
            @Value("${gpt.script.check-emergency}") String emergencyScript,
            @Value("${gpt.script.llm}") String llmScript
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
                .topP(0.3)
                .temperature(0.1)
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
