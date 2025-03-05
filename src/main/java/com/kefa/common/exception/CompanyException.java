package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class CompanyException extends CustomException{
    public CompanyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
