package com.konkuk.gp.global.exception.socket;

public class ErrorMapper {
    public static ErrorCode getCode(ErrorMessage message) {
        ErrorCode res;
        switch (message) {
            case SOCKET_ALREADY_USED:
                res = ErrorCode.SOCKET_OPEN_FAILED;
                break;
            case INVALID_MESSAGE_FORMAT:
                res = ErrorCode.INVALID_MESSAGE;
                break;
            case SOCKET_CLOSE_FAILED_INVALID:
            case SOCKET_CLOSE_FAILED_NOT_FOUND:
                res = ErrorCode.SOCKET_CLOSE_FAILED;
                break;
            case CLIENT_SOCKET_NOT_FOUND:
                res = ErrorCode.CLIENT_NOT_FOUND;
                break;
            default:
                res = ErrorCode.INTERNAL_ERROR;
                break;
        }
        return res;
    }
}
