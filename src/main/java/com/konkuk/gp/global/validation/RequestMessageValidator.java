package com.konkuk.gp.global.validation;

import com.konkuk.gp.controller.stomp.dto.ai.AiRequestDto;
import com.konkuk.gp.controller.stomp.dto.client.ClientRequestDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RequestMessageValidator  {
    public boolean supports(Class<?> clazz) {
        return ClientRequestDto.class.isAssignableFrom(clazz) || AiRequestDto.class.isAssignableFrom(clazz);
    }

    public void validate(Object target) {
        Map<String, String> errorMap = checkError(target);
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

    private Map<String, String> checkError(Object message) {
        if (message.getClass().isAssignableFrom(ClientRequestDto.class)) {
            return validateClient((ClientRequestDto)message);
        } else {
            return validateAI((AiRequestDto) message);
        }
    }

    private Map<String, String> validateClient(ClientRequestDto data) {
        Map<String, String> errors = new HashMap<>();
        if (data.getScript() == null) errors.put("script", "null");
//        if (data.getIsReal() == null) errors.put("isReal", "null");
        return errors;
    }

    private Map<String, String> validateAI(AiRequestDto data) {
        Map<String, String> errors = new HashMap<>();
        if (data.getCaption() == null) errors.put("caption", "null");
//        if (data.getIsReal() == null) errors.put("isReal", "null");
        return errors;
    }
}
