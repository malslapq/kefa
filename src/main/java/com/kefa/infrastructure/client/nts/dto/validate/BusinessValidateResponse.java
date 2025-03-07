package com.kefa.infrastructure.client.nts.dto.validate;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BusinessValidateResponse {

    private String status_code;
    private int request_cnt;
    private int valid_cnt;
    private List<BusinessValidateData> data;

}
