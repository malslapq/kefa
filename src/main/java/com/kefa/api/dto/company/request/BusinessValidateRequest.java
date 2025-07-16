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

    @Size(max = 100, message = "대표자성명은 100자 이하여야 합니다")
    private String p_nm;

    @Size(max = 100, message = "상호는 100자 이하여야 합니다")
    private String b_nm;

}