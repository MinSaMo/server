package com.konkuk.gp.core.message;

import com.konkuk.gp.client.data.ClientResponseData;
import com.konkuk.gp.global.exception.ErrorData;
import com.konkuk.gp.global.exception.ErrorMapper;
import com.konkuk.gp.global.exception.ErrorMessage;

public class MessageManager {
    public static Message<ErrorData> error(ErrorMessage message) {
        return Message.<ErrorData>builder()
                .sender(Message.SENDER_SERVER)
                .data(ErrorData.builder()
                        .code(ErrorMapper.getCode(message))
                        .errMsg(message.getMsg())
                        .build())
                .build();
    }

    public static Message<ClientResponseData> response(ClientResponseData data) {
        return Message.<ClientResponseData>builder()
                .sender(Message.SENDER_SERVER)
                .data(data)
                .build();
    }

}
