package com.kefa.common.type;

import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.PaymentException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum PaymentStatus {

    PAID("결제 완료"),
    CANCELED("결제 취소"),
    READY("결제 대기"),
    FAILED("결제 실패"),
    REFUNDED("환불 완료");

    private final String type;

    public static PaymentStatus fromMethod(String type) {
        return Arrays.stream(values())
            .filter(status -> status.getType().equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(() -> new PaymentException(ErrorCode.INVALID_PAYMENT_STATUS));
    }

}
