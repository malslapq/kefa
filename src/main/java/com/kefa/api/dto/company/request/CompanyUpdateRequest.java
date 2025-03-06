package com.kefa.api.dto.company.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyUpdateRequest {

    @NotNull
    private Long id;

    @NotBlank(message = "회사명은 필수입니다")
    private String name;

    @NotBlank(message = "주소는 필수입니다")
    private String address;

    @NotBlank(message = "업종은 필수입니다")
    private String industry;

    @PositiveOrZero(message = "매출액은 0 이상이어야 합니다.")
    private Long revenueMillion;

}
