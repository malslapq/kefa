package com.kefa.common.exception;

public class EmailException extends RuntimeException {

    private final ErrorCode errorCode;

    public EmailException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
