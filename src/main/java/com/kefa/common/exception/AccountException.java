package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class AccountException extends CustomException{

    public AccountException(ErrorCode errorCode) {
        super(errorCode);
    }

}
