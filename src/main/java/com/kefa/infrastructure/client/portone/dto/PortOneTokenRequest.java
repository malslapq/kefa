package com.kefa.infrastructure.client.portone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortOneTokenRequest {

    @JsonProperty("imp_key")
    private String impKey;
    @JsonProperty("imp_secret")
    private String impSecret;

}
