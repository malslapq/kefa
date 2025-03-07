package com.kefa.infrastructure.client.nts.dto.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BusinessStatusRequest {

    private List<String> b_no;

    public static BusinessStatusRequest of(String businessNumber) {
        return new BusinessStatusRequest(List.of(businessNumber));
    }

}
