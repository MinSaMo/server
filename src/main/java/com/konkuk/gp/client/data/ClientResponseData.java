package com.konkuk.gp.client.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientResponseData {
    private String script;
    // TODO : enum
    private String type;
    private boolean isFinish;
    private Long dialogId;
}
