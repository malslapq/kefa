package com.kefa.api.dto.company.response;

import com.kefa.domain.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyAddResponse {

    private Long id;
    private String name;
    private String businessNumber;
    private String address;
    private String industry;
    private Long revenueMillion;

    public static CompanyAddResponse from(Company company) {
        return CompanyAddResponse.builder()
            .id(company.getId())
            .name(company.getName())
            .businessNumber(company.getBusinessNumber())
            .address(company.getAddress())
            .industry(company.getIndustry())
            .revenueMillion(company.getRevenueMillion())
            .build();
    }

}