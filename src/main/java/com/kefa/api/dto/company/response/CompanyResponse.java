package com.kefa.api.dto.company.response;

import com.kefa.domain.entity.Company;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyResponse {

    private Long id;
    private String name;
    private String businessNumber;
    private String address;
    private String industry;
    private Long revenueMillion;

    /**
     * Creates a CompanyResponse DTO from a Company entity by mapping its fields.
     *
     * @param company the Company entity to convert
     * @return a CompanyResponse containing the corresponding company information
     */
    public static CompanyResponse from(Company company) {
        return CompanyResponse.builder()
            .id(company.getId())
            .name(company.getName())
            .businessNumber(company.getBusinessNumber())
            .address(company.getAddress())
            .industry(company.getIndustry())
            .revenueMillion(company.getRevenueMillion())
            .build();
    }

}
