package com.konkuk.gp.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konkuk.gp.core.message.Message;

public class Utils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Utils() {
    }

    public static <T> Message<T> getObject(final String message, Class<T> clazz) throws Exception {
        return objectMapper.readValue(message, objectMapper.getTypeFactory().constructParametricType(Message.class, clazz));
    }

    public static <T> String getString(final Message<T> message) throws Exception {
        return objectMapper.writeValueAsString(message);
    }
}
