package com.kefa.api.dto.company.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BusinessNumberValidateRequest {

    @NotBlank(message = "사업자 등록 번호는 필수입니다.")
    private String b_no;

}
