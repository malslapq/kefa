package com.kefa.infrastructure.client.nts.service;

import com.kefa.api.dto.company.request.BusinessNumberValidateRequest;
import com.kefa.api.dto.company.request.BusinessValidateRequest;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.NtsException;
import com.kefa.infrastructure.client.nts.config.NtsApiProperties;
import com.kefa.infrastructure.client.nts.dto.status.BusinessStatusRequest;
import com.kefa.infrastructure.client.nts.dto.status.BusinessStatusResponse;
import com.kefa.infrastructure.client.nts.dto.validate.BusinessValidateResponse;
import com.kefa.infrastructure.client.nts.handler.NtsApiErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NtsBusinessValidationClient {

    private static final String SERVICE_KEY_PARAM_NAME = "serviceKey";
    private final NtsApiErrorHandler ntsApiErrorHandler;
    private final RestClient restClient;
    private final NtsApiProperties properties;

    public BusinessValidateResponse validateBusinessInfo(BusinessValidateRequest request) {
        try {
            Map<String, List<BusinessValidateRequest>> requestBody = Map.of(
                "businesses", List.of(request)
            );

            String url = buildNtsApiUrl(properties.getValidatePath());

            return restClient.post()
                .uri(url)
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

            String url = buildNtsApiUrl(properties.getStatusPath());

            return restClient.post()
                .uri(url)
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

    private String buildNtsApiUrl(String apiPath) {
        return UriComponentsBuilder.fromUriString(properties.getBaseUrl())
            .path(apiPath)
            .queryParam(SERVICE_KEY_PARAM_NAME, properties.getKey())
            .build()
            .toUriString();
    }
}