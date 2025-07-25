package com.kefa.common.type;

import com.kefa.common.exception.ContactException;
import com.kefa.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum ContactStatus {

    PENDING("대기 중"),
    RESOLVED("해결");


    private final String description;

    /**
     * Converts a string to the corresponding {@code ContactStatus} enum constant, ignoring case.
     *
     * @param value the string representation of the contact status
     * @return the matching {@code ContactStatus} constant
     * @throws ContactException if the input is null or does not match any status
     */
    public static ContactStatus from(String value) {
        if (value == null)
            throw new ContactException(ErrorCode.INVALID_CONTACT_STATUS);

        return Arrays.stream(ContactStatus.values())
            .filter(status -> status.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new ContactException(ErrorCode.INVALID_CONTACT_STATUS));
    }

}