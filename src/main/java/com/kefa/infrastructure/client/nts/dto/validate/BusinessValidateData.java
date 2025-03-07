package com.kefa.infrastructure.client.nts.dto.validate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kefa.infrastructure.client.nts.dto.status.BusinessStatusData;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BusinessValidateData {

    @JsonProperty("b_no")
    private String businessNumber;    // 사업자등록번호

    @JsonProperty("valid")
    private String valid;             // 검증 결과 코드(01: 일치 / 02: 불일치)

    @JsonProperty("valid_msg")
    private String validMessage;      // 검증 결과 메시지 (불일치 시 "확인할 수 없습니다")

    @JsonProperty("request_param")
    private BusinessValidateRequestParam requestParam;      // 검증 데이터

    @JsonProperty("status")
    private BusinessStatusData status;      // 사업자 상태 정보

}
