package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class CipherException extends CustomException {

    public CipherException(ErrorCode errorCode) {
        super(errorCode);
    }

}
