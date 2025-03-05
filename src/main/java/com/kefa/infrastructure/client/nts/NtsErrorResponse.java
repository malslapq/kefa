package com.kefa.infrastructure.client.nts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NtsErrorResponse {

    @JsonProperty("status_code")
    private String statusCode;

    @JsonProperty("match_cnt")
    private Integer matchCount;

    @JsonProperty("request_cnt")
    private Integer requestCount;

    private List<BusinessStatusData> data;

}
