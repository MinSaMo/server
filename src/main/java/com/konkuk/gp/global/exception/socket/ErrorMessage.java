package com.konkuk.gp.global.exception.socket;

public enum ErrorMessage {
    SOCKET_ALREADY_USED("Socket Already Used"),
    INVALID_MESSAGE_FORMAT("Invalid Message Format"),
    SOCKET_CLOSE_FAILED_NOT_FOUND("Socket Not Found"),
    SOCKET_CLOSE_FAILED_INVALID("Invalid Socket"),
    CLIENT_SOCKET_NOT_FOUND("Can't Receive, No Client Socket"),
    ;

    private final String msg;

    public String getMsg() {
        return msg;
    }

    ErrorMessage(String msg) {
        this.msg = msg;
    }
}
