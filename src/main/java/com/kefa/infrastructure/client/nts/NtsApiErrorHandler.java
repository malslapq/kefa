package com.kefa.infrastructure.client.nts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.NtsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class NtsApiErrorHandler {

    private final ObjectMapper objectMapper;

    public void errorHandler(HttpRequest request, ClientHttpResponse response) {

        NtsErrorResponse ntsErrorResponse = getNtsErrorResponse(response);

        validateBusinessNumberNotFound(ntsErrorResponse);

        try {
            ErrorCode errorCode = getErrorCode(response.getStatusCode().value());
            throw new NtsException(errorCode);
        } catch (IOException e) {
            throw new NtsException(ErrorCode.NTS_HTTP_ERROR);
        }

    }

    private NtsErrorResponse getNtsErrorResponse(ClientHttpResponse response) {
        try {
            return objectMapper.readValue(response.getBody(), NtsErrorResponse.class);
        } catch (Exception e) {
            throw new NtsException(ErrorCode.NTS_PARSING_ERROR);
        }
    }

    private void validateBusinessNumberNotFound(NtsErrorResponse response) {

        if (response == null) {
            throw new NtsException(ErrorCode.BUSINESS_NUMBER_NOT_FOUND);
        }

        if (response.getData().isEmpty()) {
            throw new NtsException(ErrorCode.BUSINESS_NUMBER_NOT_FOUND);
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
