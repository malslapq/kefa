package com.kefa.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //인증 관련
    INVALID_EMAIL_VERIFICATION_TOKEN(400, "유효하지 않은 메일 인증 토큰입니다."),
    INVALID_ROLE(400, "유효하지 않은 권한입니다."),
    DUPLICATE_EMAIL(400, "이미 존재하는 이메일입니다"),
    INVALID_EMAIL_FORMAT(400, "잘못된 이메일 형식입니다"),
    INVALID_PASSWORD_FORMAT(400, "비밀번호는 8자 이상, 영문/숫자/특수문자를 포함해야 합니다"),
    UNSUPPORTED_SOCIAL_PROVIDER(400, "지원하지 않는 소셜 로그인 제공자입니다"),
    ACCOUNT_NOT_FOUND(404, "계정을 찾을 수 없습니다"),
    EMAIL_VERIFICATION_REQUIRED(403, "이메일 인증이 필요합니다"),
    UNAUTHORIZED(401, "인증이 필요한 서비스입니다"),
    ACCESS_DENIED(403, "접근 권한이 없습니다"),

    // jwt 에러
    INVALID_JWT_SIGNATURE(401, "유효하지 않은 JWT 서명입니다"),
    MALFORMED_JWT_TOKEN(401, "잘못된 JWT 토큰입니다"),
    EXPIRED_JWT_TOKEN(401, "만료된 JWT 토큰입니다"),
    UNSUPPORTED_JWT_TOKEN(401, "지원되지 않는 JWT 토큰입니다"),
    EMPTY_JWT_TOKEN(401, "JWT 토큰이 비어있습니다"),

    // 서버 에러
    SERVER_ERROR(500, "서버 내부 오류 발생"),
    ENCRYPTION_FAILED(500, "데이터 암호화 처리 중 오류가 발생했습니다"),
    DECRYPTION_FAILED(500, "데이터 복호화 처리 중 오류가 발생했습니다");


    private final int status;
    private final String message;

}
