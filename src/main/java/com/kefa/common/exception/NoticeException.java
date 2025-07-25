package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class NoticeException extends CustomException {

    public NoticeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
