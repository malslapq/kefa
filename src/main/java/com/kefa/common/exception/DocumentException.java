package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class DocumentException extends CustomException {
    public DocumentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
