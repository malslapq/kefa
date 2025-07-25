package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class ContactException extends CustomException {

    public ContactException(ErrorCode errorCode) {
        super(errorCode);
    }
}
