package com.konkuk.gp.global.logger;

import com.konkuk.gp.global.logger.message.client.ClientLog;
import com.konkuk.gp.service.enums.ChatType;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Scope("prototype")
@Setter
public class ClientLogProperty implements ClientLog {
    private UUID uuid;
    private Long memberId;
    private Long dialogId;
    private String script;
    private ChatType intense;
    private String prompt;
    private String reply;
    private double responseTime;

    @Override
    public Map<String, Object> getIntenseLogMessage() {
        Map<String, Object> res = this.base();
        res.put("script", script);
        res.put("intense", intense.getName());
        return res;
    }

    @Override
    public Map<String, Object> getPromptLogMessage() {
        Map<String, Object> res = this.base();
        res.put("intense", intense.getName());
        res.put("prompt", prompt);
        return res;
    }

    @Override
    public Map<String, Object> getReplyLogMessage() {
        Map<String, Object> res = this.base();
        res.put("reply", reply);
        return res;
    }

    @Override
    public Map<String, Object> getScriptLogMessage() {
        Map<String, Object> res = this.base();
        res.put("script", script);
        res.put("dialogId", String.valueOf(dialogId));
        return res;
    }

    private Map<String, Object> base() {
        Map<String, Object> res = new HashMap<>();
        res.put("timestamp", LocalDateTime.now().toString());
        res.put("uuid", uuid.toString());
        res.put("memberId", String.valueOf(memberId));
        return res;
    }

}
