package com.konkuk.gp.global.exception;

public class NotFoundException extends RuntimeException {
    public static final NotFoundException MEMBER_NOT_FOUND = new NotFoundException("MEMBER_NOT_FOUND");

    public NotFoundException(String message) {
        super(message);
    }
}
