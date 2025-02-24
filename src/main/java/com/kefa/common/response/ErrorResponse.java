package com.kefa.common.response;

import com.kefa.common.exception.ErrorCode;

public record ErrorResponse(int status, String message) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getStatus(), errorCode.getMessage());
    }

}