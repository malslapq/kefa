package com.kefa.infrastructure.client.nts.handler;

import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.NtsException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NtsApiErrorHandler {

    public void errorHandler(HttpRequest request, ClientHttpResponse response) {

        try {
            ErrorCode errorCode = getErrorCode(response.getStatusCode().value());
            throw new NtsException(errorCode);
        } catch (IOException e) {
            throw new NtsException(ErrorCode.NTS_HTTP_ERROR);
        }
    }

    private ErrorCode getErrorCode(int httpStatus) {
        return switch (httpStatus) {
            case 400 -> ErrorCode.NTS_BAD_JSON_REQUEST;
            case 404 -> ErrorCode.NTS_SERVICE_NOT_FOUND;
            case 411 -> ErrorCode.NTS_MISSING_PARAMETER;
            case 413 -> ErrorCode.NTS_TOO_MANY_BUSINESS_NUMBERS;
            case 500 -> ErrorCode.NTS_INTERNAL_ERROR;
            default -> ErrorCode.NTS_HTTP_ERROR;
        };
    }

}
