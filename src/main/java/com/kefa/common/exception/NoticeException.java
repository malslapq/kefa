package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class NoticeException extends CustomException {

    /**
     * Constructs a new NoticeException with the specified error code.
     *
     * @param errorCode the error code associated with this exception
     */
    public NoticeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
