package com.kefa.infrastructure.client.nts.dto.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessStatusData {

    private final static String STATUS_ACTIVE = "계속사업자";
    private final static String NOT_FOUND_BUSINESS_NUMBER = "국세청에 등록되지 않은 사업자등록번호입니다.";

    @JsonProperty("b_no")
    private String businessNumber;        // 사업자번호

    @JsonProperty("b_stt")
    private String businessStatus;        // 납세자 상태

    @JsonProperty("b_stt_cd")
    private String businessStatusCode;    // 납세자 상태 코드

    @JsonProperty("tax_type")
    private String taxType;              // 과세유형

    @JsonProperty("tax_type_cd")
    private String taxTypeCode;          // 과세유형 코드

    @JsonProperty("end_dt")
    private String endDate;              // 폐업일

    @JsonProperty("utcc_yn")
    private String utccYn;               // 단위과세전환폐업여부

    @JsonProperty("tax_type_change_dt")
    private String taxTypeChangeDate;    // 과세유형전환일자

    @JsonProperty("invoice_apply_dt")
    private String invoiceApplyDate;     // 세금계산서적용일자

    @JsonProperty("rbf_tax_type")
    private String previousTaxType;      // 과세유형전환전과세유형

    @JsonProperty("rbf_tax_type_cd")
    private String previousTaxTypeCode;  // 과세유형전환전과세유형코드

    public boolean isActive() {
        return STATUS_ACTIVE.equals(businessStatus);
    }

    public boolean isNotRegistered() {
        return NOT_FOUND_BUSINESS_NUMBER.equals(taxType);
    }

}
