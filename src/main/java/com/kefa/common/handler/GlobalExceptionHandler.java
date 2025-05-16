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
        AccountException.class,
        AdminException.class,
        AuthenticationException.class,
        CipherException.class,
        CompanyException.class,
        ConsultingSessionException.class,
        DevelopmentItemException.class,
        DocumentException.class,
        EmailException.class,
        FeedbackException.class,
        JwtAuthenticationException.class,
        NtsException.class,
        OAuth2Exception.class,
        S3FileUploadException.class
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
