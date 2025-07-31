package com.kefa.infrastructure.client.portone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.PortOneException;
import com.kefa.infrastructure.client.portone.config.PortOneProperties;
import com.kefa.infrastructure.client.portone.dto.*;
import com.kefa.infrastructure.client.portone.handler.PortOneErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PortOneClient {

    private static final String GET_TOKEN_URL = "/users/getToken";
    private static final String CANCEL_PAYMENT_URL = "/payments/cancel";
    private static final String GET_PAYMENT_BALANCE_PREFIX_URL = "/payments";
    private static final String GET_PAYMENT_BALANCE_SUFFIX_URL = "/balance";
    private final PortOneProperties portOneProperties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final PortOneErrorHandler portOneErrorHandler;

    public PortOneCancelResponse cancelPayment(PortOneCancelRequest request) {

        PortOneCancelResponse response;
        try {
            response = restClient.post()
                .uri(portOneProperties.getUrl() + CANCEL_PAYMENT_URL)
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(request))
                .retrieve()
                .onStatus(HttpStatusCode::isError, portOneErrorHandler::errorHandler)
                .body(PortOneCancelResponse.class);
        } catch (JsonProcessingException e) {
            throw new PortOneException(ErrorCode.FAILED_JSON_FORMAT);
        }

        return response;
    }

    public PortOnePaymentBalanceResponse getPaymentDetail(String impUid) {
        return restClient.get()
            .uri(portOneProperties.getUrl() + buildPortOneApiUrl(impUid))
            .header("Authorization", getAccessToken())
            .retrieve()
            .onStatus(HttpStatusCode::isError, portOneErrorHandler::errorHandler)
            .body(PortOnePaymentBalanceResponse.class);
    }

    public String getAccessToken() {
        PortOneTokenRequest request = PortOneTokenRequest.builder()
            .impKey(portOneProperties.getApiKey())
            .impSecret(portOneProperties.getApiSecret())
            .build();

        PortOneTokenResponse response;
        try {

            response = restClient.post()
                .uri(portOneProperties.getUrl() + GET_TOKEN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(request))
                .retrieve()
                .body(PortOneTokenResponse.class);

        } catch (JsonProcessingException e) {
            throw new PortOneException(ErrorCode.FAILED_JSON_FORMAT);
        }

        if (response == null || response.getResponse().getAccessToken() == null) {
            throw new PortOneException(ErrorCode.FAILED_ACCESS_TOKEN_GENERATION);
        }

        return response.getResponse().getAccessToken();
    }

    private String buildPortOneApiUrl(String impUid) {
        return UriComponentsBuilder.fromUriString(portOneProperties.getUrl())
            .path(GET_PAYMENT_BALANCE_PREFIX_URL)
            .pathSegment(impUid)
            .path(GET_PAYMENT_BALANCE_SUFFIX_URL)
            .build().toUriString();
    }

}