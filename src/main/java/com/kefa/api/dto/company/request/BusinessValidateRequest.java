package com.kefa.api.dto.company.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessValidateRequest {

    @NotBlank(message = "사업자등록번호는 필수입니다")
    @Pattern(regexp = "^[0-9]{10}$", message = "사업자등록번호는 10자리 숫자여야 합니다")
    private String b_no;

    @Size(max = 8, message = "개업일자는 8자리 이하여야 합니다")
    @Pattern(regexp = "^[0-9]*$", message = "개업일자는 숫자만 입력 가능합니다")
    private String start_dt;

    @Size(max = 100, message = "대표자성명은 100자 이하여야 합니다")
    private String p_nm;

    @Size(max = 100, message = "대표자성명2는 100자 이하여야 합니다")
    private String p_nm2;

    @Size(max = 100, message = "상호는 100자 이하여야 합니다")
    private String b_nm;

    @Size(max = 13, message = "법인번호는 13자리 이하여야 합니다")
    @Pattern(regexp = "^[0-9]*$", message = "법인번호는 숫자만 입력 가능합니다")
    private String corp_no;

    @Size(max = 100, message = "주업태명은 100자 이하여야 합니다")
    private String b_sector;

    @Size(max = 100, message = "주종목명은 100자 이하여야 합니다")
    private String b_type;

    @Size(max = 200, message = "사업장주소는 200자 이하여야 합니다")
    private String b_adr;


}