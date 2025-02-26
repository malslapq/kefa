package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends CustomException {
    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
