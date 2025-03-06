package com.kefa.api.dto.company.request;

import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.Company;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyAddRequest {

    @NotBlank(message = "회사명은 필수입니다.")
    private String name;

    @NotBlank(message = "사업자번호는 필수입니다")
    @Pattern(regexp = "^[0-9]{10}$", message = "사업자번호는 10자리 숫자여야 합니다")
    private String businessNumber;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotBlank(message = "업종은 필수입니다.")
    private String industry;

    @PositiveOrZero(message = "매출액은 0 이상이어야 합니다.")
    private Long revenueMillion;

    public static Company toEntity(CompanyAddRequest request, Account account) {
        return Company.builder()
            .account(account)
            .name(request.getName())
            .businessNumber(request.getBusinessNumber())
            .address(request.getAddress())
            .industry(request.getIndustry())
            .revenueMillion(request.getRevenueMillion())
            .build();
    }

}
