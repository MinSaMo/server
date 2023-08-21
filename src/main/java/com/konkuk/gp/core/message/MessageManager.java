package com.konkuk.gp.core.message;

import com.konkuk.gp.domain.client.dto.ClientResponseDto;
import com.konkuk.gp.global.exception.ErrorData;
import com.konkuk.gp.global.exception.ErrorMapper;
import com.konkuk.gp.global.exception.ErrorMessage;

public class MessageManager {
    public static Message<ErrorData> error(ErrorMessage message) {
        return Message.of(ErrorData.builder()
                .code(ErrorMapper.getCode(message))
                .errMsg(message.getMsg())
                .build());
    }

    public static Message<ClientResponseDto> response(ClientResponseDto data) {
        return Message.of(data);
    }

}
