package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class DevelopmentItemException extends CustomException{

    public DevelopmentItemException(ErrorCode errorCode) {
        super(errorCode);
    }

}
