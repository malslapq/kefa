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

    /**
     * Handles custom application exceptions and returns a standardized error response.
     *
     * This method intercepts various custom exceptions thrown within the application, logs the error details,
     * and constructs an HTTP response containing an error payload based on the exception's error code.
     *
     * @param e the custom exception to handle
     * @return a ResponseEntity containing an ApiResponse with error details and the appropriate HTTP status
     */
    @ExceptionHandler({
        AccountException.class,
        AdminException.class,
        AuthenticationException.class,
        CipherException.class,
        CompanyException.class,
        ConsultingSessionException.class,
        ContactException.class,
        CustomException.class,
        DevelopmentItemException.class,
        DocumentException.class,
        EmailException.class,
        FeedbackException.class,
        JwtAuthenticationException.class,
        NoticeException.class,
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
