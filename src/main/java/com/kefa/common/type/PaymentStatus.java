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

    private final String description;

    /**
     * Returns the PaymentStatus enum constant whose description matches the given string, ignoring case.
     *
     * @param type the description string to match against payment statuses
     * @return the PaymentStatus with a matching description
     * @throws PaymentException if no matching PaymentStatus is found
     */
    public static PaymentStatus fromMethod(String type) {
        return Arrays.stream(values())
            .filter(status -> status.getDescription().equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(() -> new PaymentException(ErrorCode.INVALID_PAYMENT_STATUS));
    }

}
