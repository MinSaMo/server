package com.konkuk.gp.core.message;

import com.konkuk.gp.core.message.dto.client.ClientResponseDto;
import com.konkuk.gp.global.exception.socket.ErrorCode;
import com.konkuk.gp.global.exception.socket.ErrorData;
import com.konkuk.gp.global.exception.socket.ErrorMapper;
import com.konkuk.gp.global.exception.socket.ErrorMessage;

public class MessageUtils {
    public static Message<ErrorData> error(ErrorMessage message) {
        return Message.of(ErrorData.builder()
                .code(ErrorMapper.getCode(message))
                .errMsg(message.getMsg())
                .build(), Message.STATUS_NOT_OK);
    }

    public static Message<ErrorData> error(String message, ErrorCode code) {
        return Message.of(ErrorData.builder()
                .code(code)
                .errMsg(message)
                .build(), Message.STATUS_NOT_OK);
    }

    public static Message<ClientResponseDto> response(ClientResponseDto data) {
        return Message.of(data, Message.STATUS_OK);
    }

}
