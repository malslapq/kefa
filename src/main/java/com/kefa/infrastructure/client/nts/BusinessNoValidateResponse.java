package com.kefa.infrastructure.client.nts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessNoValidateResponse {

    private String status_code;
    private int match_cnt;
    private int request_cnt;
    private List<BusinessStatusData> data;

}
