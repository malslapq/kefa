package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class EmailException extends CustomException {

    public EmailException(ErrorCode errorCode) {
        super(errorCode);
    }

}
