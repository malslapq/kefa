package com.kefa.infrastructure.client.nts.service;

import com.kefa.api.dto.company.request.BusinessNumberValidateRequest;
import com.kefa.api.dto.company.request.BusinessValidateRequest;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.NtsException;
import com.kefa.infrastructure.client.nts.config.NtsApiProperties;
import com.kefa.infrastructure.client.nts.dto.status.BusinessStatusResponse;
import com.kefa.infrastructure.client.nts.dto.status.BusinessStatusRequest;
import com.kefa.infrastructure.client.nts.dto.validate.BusinessValidateResponse;
import com.kefa.infrastructure.client.nts.handler.NtsApiErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NtsBusinessValidationClient {

    private final NtsApiErrorHandler ntsApiErrorHandler;
    private final RestClient restClient;
    private final NtsApiProperties properties;
    private static final String API_PATH = "/api/nts-businessman/v1/status";
    private static final String VALIDATE_API_PATH = "/api/nts-businessman/v1/validate";

    public BusinessValidateResponse validateBusinessInfo(BusinessValidateRequest request) {
        try {
            Map<String, List<BusinessValidateRequest>> requestBody = Map.of(
                "businesses", List.of(request)
            );

            return restClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path(VALIDATE_API_PATH)
                    .queryParam("serviceKey", properties.getKey())
                    .build()
                )
                .body(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, ntsApiErrorHandler::errorHandler)
                .body(BusinessValidateResponse.class);
        } catch (RestClientException e) {
            log.error("국세청 API 에러: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            throw new NtsException(ErrorCode.NTS_HTTP_ERROR);
        }
    }

    public BusinessStatusResponse validateBusinessNumber(BusinessNumberValidateRequest request) {
        try {

            return restClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path(API_PATH)
                    .queryParam("serviceKey", properties.getKey())
                    .build()
                )
                .body(BusinessStatusRequest.of(request.getB_no()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, ntsApiErrorHandler::errorHandler)
                .body(BusinessStatusResponse.class);

        } catch (RestClientException e) {
            log.error("국세청 API 에러: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            throw new NtsException(ErrorCode.NTS_HTTP_ERROR);
        }
    }


}
