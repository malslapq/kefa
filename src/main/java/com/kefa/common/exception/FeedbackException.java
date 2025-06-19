package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class FeedbackException extends CustomException {
    public FeedbackException(ErrorCode errorCode) {
        super(errorCode);
    }
}
