package com.konkuk.gp.global.utils.convert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konkuk.gp.core.message.Message;
import com.konkuk.gp.core.message.dto.client.ClientRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

@Component
@RequiredArgsConstructor
public class ClientMessageConverter implements Converter<TextMessage, Message<ClientRequestDto>> {

    private final ObjectMapper objectMapper;

    @Override
    public Message<ClientRequestDto> convert(TextMessage source) {
        try {
            return objectMapper.readValue(source.getPayload(), objectMapper.getTypeFactory().constructParametricType(Message.class, ClientRequestDto.class));
        } catch (JsonProcessingException e) {
            throw new ClassCastException(e.getMessage());
        }
    }
}
