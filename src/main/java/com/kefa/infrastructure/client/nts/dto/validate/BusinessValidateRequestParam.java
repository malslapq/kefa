package com.kefa.infrastructure.client.nts.dto.validate;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BusinessValidateRequestParam {

    private String b_no;         // 사업자등록번호
    private String start_dt;     // 개업일자
    private String p_nm;         // 대표자 성명
    private String p_nm2;        // 공동대표자 성명
    private String b_nm;         // 상호명
    private String corp_no;      // 법인등록번호
    private String b_sector;     // 주업태명
    private String b_type;       // 주종목명
    private String b_adr;        // 사업장 주소

}
