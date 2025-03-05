package com.kefa.infrastructure.client.nts;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NtsApiRequest {

    private List<String> b_no;

    public static NtsApiRequest of(String businessNumber) {
        return new NtsApiRequest(List.of(businessNumber));
    }

}
