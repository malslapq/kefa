package com.kefa.infrastructure.client.nts;

import com.kefa.api.dto.company.request.BusinessNumberValidateRequest;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.NtsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationBusinessNumberClient {

    private final NtsApiErrorHandler ntsApiErrorHandler;
    private final RestClient restClient;
    private final NtsApiProperties properties;
    private static final String API_PATH = "/api/nts-businessman/v1/status";

    public BusinessNoValidateResponse validateBusinessNumber(BusinessNumberValidateRequest request) {
        try {

            return restClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path(API_PATH)
                    .queryParam("serviceKey", properties.getKey())
                    .build()
                )
                .body(NtsApiRequest.of(request.getB_no()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, ntsApiErrorHandler::errorHandler)
                .body(BusinessNoValidateResponse.class);

        } catch (RestClientException e) {
            log.error("국세청 API 에러: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            throw new NtsException(ErrorCode.NTS_HTTP_ERROR);
        }
    }


}
