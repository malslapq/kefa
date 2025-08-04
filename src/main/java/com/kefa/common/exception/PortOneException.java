package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class PortOneException extends CustomException{

    public PortOneException(ErrorCode errorCode) {
        super(errorCode);
    }
}
