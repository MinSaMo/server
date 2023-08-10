package com.konkuk.gp.client.data;

import lombok.Getter;

@Getter
public enum TriggerType {
    BY_CAPTION(2),
    BY_USER(1),
    BY_EMERGENCY(0);

    private final int val;

    TriggerType(int val) {
        this.val = val;
    }
}
