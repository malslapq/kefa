package com.kefa.infrastructure.client.portone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PortOneTokenResponse {

    private int code;

    private String message;

    private ResponseData response;

    @NoArgsConstructor
    @Getter
    public static class ResponseData {

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("now")
        private Long now;

        @JsonProperty("expired_at")
        private Long expiredAt;
    }
}
