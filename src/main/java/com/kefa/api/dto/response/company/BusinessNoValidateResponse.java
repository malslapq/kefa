package com.kefa.api.dto.response.company;

import lombok.Getter;

import java.util.List;

@Getter
public class BusinessNoValidateResponse {

    private String status_code;
    private int match_cnt;
    private int request_cnt;
    private List<BusinessData> data;

    @Getter
    public static class BusinessData {
        private String b_no;                // 사업자번호
        private String b_stt;              // 납세자상태(명칭)
        private String b_stt_cd;           // 납세자상태(코드)
        private String tax_type;           // 과세유형(명칭)
        private String tax_type_cd;        // 과세유형(코드)
        private String end_dt;             // 폐업일
        private String utcc_yn;            // 단위과세전환폐업여부
        private String tax_type_change_dt; // 과세유형전환일자
        private String invoice_apply_dt;   // 세금계산서적용일자
        private String rbf_tax_type;       // 과세유형(명칭)
        private String rbf_tax_type_cd;    // 과세유형(코드)
    }

}
