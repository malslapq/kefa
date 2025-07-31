package com.kefa.infrastructure.client.portone.handler;

import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.PortOneException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PortOneErrorHandler {

    public void errorHandler(HttpRequest request, ClientHttpResponse response) {

        try {
            ErrorCode errorCode = getErrorCode(response.getStatusCode().value());
            throw new PortOneException(errorCode);
        } catch (IOException e) {
            throw new PortOneException(ErrorCode.PORT_ONE_HTTP_ERROR);
        }
    }

    private ErrorCode getErrorCode(int httpStatus) {
        return switch (httpStatus) {
            case 401 -> ErrorCode.INVALID_ACCESS_TOKEN;
            case 404 -> ErrorCode.INVALID_IMP_UID;
            case 405 -> ErrorCode.METHOD_NOT_ALLOWED;
            default -> ErrorCode.PORT_ONE_HTTP_ERROR;
        };
    }

}
