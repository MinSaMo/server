package com.konkuk.daila.global.exception;

public class GptRemoteServerError extends RuntimeException{
    public static final GptRemoteServerError NOT_STABLE = new GptRemoteServerError("Gpt server is not stable");

    public GptRemoteServerError(String message) {
        super(message);
    }
}
