package com.kefa.common.type;

import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.PaymentException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum PaymentOption {

    CARD("카드"),
    VIRTUAL_ACCOUNT("가상계좌"),
    ACCOUNT_TRANSFER("계좌이체"),
    PHONE("휴대폰"),
    GIFT_CERTIFICATE("상품권"),
    EASY_PAYMENT("간편결제");

    private final String description;

    /**
     * Returns the PaymentOption corresponding to the given description string, ignoring case.
     *
     * @param method the payment method description to match
     * @return the matching PaymentOption
     * @throws PaymentException if no PaymentOption matches the provided description
     */
    public static PaymentOption fromMethod(String method) {
        return Arrays.stream(values())
            .filter(option -> option.getDescription().equalsIgnoreCase(method))
            .findFirst()
            .orElseThrow(() -> new PaymentException(ErrorCode.INVALID_PAYMENT_OPTION));
    }

}
