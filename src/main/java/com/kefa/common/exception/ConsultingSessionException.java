package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class ConsultingSessionException extends CustomException {

    public ConsultingSessionException(ErrorCode errorCode) {
        super(errorCode);
    }

}
