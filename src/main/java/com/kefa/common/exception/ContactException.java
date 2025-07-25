package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class ContactException extends CustomException {

    /**
     * Constructs a new ContactException with the specified error code.
     *
     * @param errorCode the error code representing the specific contact-related error
     */
    public ContactException(ErrorCode errorCode) {
        super(errorCode);
    }
}
