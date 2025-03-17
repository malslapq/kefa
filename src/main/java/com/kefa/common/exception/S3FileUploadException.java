package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class S3FileUploadException extends CustomException{
    public S3FileUploadException(ErrorCode errorCode) {
        super(errorCode);
    }
}
