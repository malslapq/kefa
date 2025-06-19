package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class AdminException extends CustomException {

    public AdminException(ErrorCode errorCode) {
        super(errorCode);
    }

}
