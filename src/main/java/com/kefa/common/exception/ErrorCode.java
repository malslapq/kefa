package com.kefa.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // jwt 에러
    INVALID_JWT_SIGNATURE(401, "유효하지 않은 JWT 서명입니다"),
    MALFORMED_JWT_TOKEN(401, "잘못된 JWT 토큰입니다"),
    EXPIRED_JWT_TOKEN(401, "만료된 JWT 토큰입니다"),
    UNSUPPORTED_JWT_TOKEN(401, "지원되지 않는 JWT 토큰입니다"),
    EMPTY_JWT_TOKEN(401, "JWT 토큰이 비어있습니다"),

    // 서버 에러
    SERVER_ERROR(500, "서버 내부 오류 발생");


    private final int status;
    private final String message;

}
