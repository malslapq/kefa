package com.kefa.api.dto.company.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BusinessNumberValidateRequest {

    @NotBlank(message = "사업자번호는 필수입니다")
    @Pattern(regexp = "^[0-9]{10}$", message = "사업자번호는 10자리 숫자여야 합니다")
    private String b_no;

}
