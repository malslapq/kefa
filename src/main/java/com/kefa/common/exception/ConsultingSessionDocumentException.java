package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class ConsultingSessionDocumentException extends CustomException {
    public ConsultingSessionDocumentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
