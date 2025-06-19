package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class PaymentException extends CustomException {

    public PaymentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
