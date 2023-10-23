package com.konkuk.gp.global.validation;

import com.konkuk.gp.global.message.Message;
import com.konkuk.gp.controller.stomp.dto.ai.AiRequestDto;
import com.konkuk.gp.controller.stomp.dto.client.ClientRequestDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RequestMessageValidator  {
    public boolean supports(Class<?> clazz) {
        return Message.class.isAssignableFrom(clazz);
    }

    public void validate(Object target) {
        Message message = (Message) target;
        if (message.getData() == null) {
            throw new IllegalArgumentException("mapping error");
        }
        Map<String, String> errorMap = validate(message);
        if (!errorMap.isEmpty()) {
            StringBuilder sb = new StringBuilder("{");
            for (String key : errorMap.keySet()) {
                sb.append("\"").append(key).append("\"");
                sb.append(":\"").append(errorMap.get(key)).append("\",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("}");
            throw new IllegalArgumentException(sb.toString());
        }
    }

    private Map<String, String> validate(Message message) {
        if (message.getData().getClass().isAssignableFrom(ClientRequestDto.class)) {
            return validateClient(message);
        } else {
            return validateAI(message);
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
