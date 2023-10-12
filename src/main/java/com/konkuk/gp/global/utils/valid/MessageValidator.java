package com.konkuk.gp.global.utils.valid;

import com.konkuk.gp.core.message.Message;
import com.konkuk.gp.core.message.dto.ai.AiRequestDto;
import com.konkuk.gp.core.message.dto.client.ClientRequestDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageValidator {

    public void validate(Message<?> message) {
        Object data = message.getData();
        Map<String, String> errors;
        if (data instanceof ClientRequestDto) {
            errors = validateClient((Message<ClientRequestDto>) message);
        } else {
            errors = validateAI((Message<AiRequestDto>) message);
        }

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder("{");
            for (String key : errors.keySet()) {
                sb.append("\"").append(key).append("\"");
                sb.append(":\"").append(errors.get(key)).append("\",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("}");
            throw new IllegalArgumentException(sb.toString());
        }
    }

    private Map<String, String> validateClient(Message<ClientRequestDto> message) {
        Map<String, String> errors = new HashMap<>();
        if (!message.getSender().equals(Message.SENDER_CLIENT)) errors.put("sender", "sender not valid");
        ClientRequestDto data = message.getData();
        if (data.getScript() == null) errors.put("script", "null");
        if (data.getDialogId() == null) errors.put("dialogId", "null");
        if (data.getIsReal() == null) errors.put("isReal", "null");
        return errors;
    }

    private Map<String, String> validateAI(Message<AiRequestDto> message) {
        Map<String, String> errors = new HashMap<>();
        if (!message.getSender().equals(Message.SENDER_CLIENT)) errors.put("sender", "sender not valid");
        AiRequestDto data = message.getData();
        if (data.getCaption() == null) errors.put("caption", "null");
        if (data.getIsReal() == null) errors.put("isReal", "null");
        return errors;
    }
}
