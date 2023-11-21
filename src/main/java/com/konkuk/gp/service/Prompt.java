package com.konkuk.gp.service;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Prompt {
    private String script;
    private double topP;
    private double temperature;

    @Builder
    public Prompt(String script, double topP, double temperature) {
        this.script = script;
        this.topP = topP;
        this.temperature = temperature;
    }
}
