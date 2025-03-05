package com.kefa.common.handler;

import com.kefa.common.exception.*;
import com.kefa.common.response.ApiResponse;
import com.kefa.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        NtsException.class,
        CompanyException.class,
        EmailException.class,
        CipherException.class,
        AuthenticationException.class,
        JwtAuthenticationException.class
    })
    public ResponseEntity<ApiResponse<?>> handleException(CustomException e) {
        log.error("{} {} - {}",
            e.getClass().getSimpleName(),  // 실제 예외 클래스명으로 하기 위함
            e.getErrorCode().name(),
            e.getMessage()
        );
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ApiResponse.error(ErrorResponse.of(errorCode)));
    }

}
