package com.kefa.common.exception;

public class JwtAuthenticationException extends CustomException{

    public JwtAuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

}
