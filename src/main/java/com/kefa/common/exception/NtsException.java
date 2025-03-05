package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class NtsException extends CustomException{
    public NtsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
