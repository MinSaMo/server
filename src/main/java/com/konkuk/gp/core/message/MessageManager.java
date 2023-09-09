package com.konkuk.gp.core.message;

import com.konkuk.gp.core.message.dto.client.ClientResponseDto;
import com.konkuk.gp.global.exception.socket.ErrorData;
import com.konkuk.gp.global.exception.socket.ErrorMapper;
import com.konkuk.gp.global.exception.socket.ErrorMessage;

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
