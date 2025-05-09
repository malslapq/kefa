package com.kefa.common.exception;

import lombok.Getter;

@Getter
public class OAuth2Exception extends CustomException{

    public OAuth2Exception(ErrorCode errorCode) {
        super(errorCode);
    }

}
