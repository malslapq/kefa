package com.kefa.common.util;

import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public class RequestUtils {

    private static final String USER_AGENT_HEADER = "User-Agent";

    public static String generateDeviceIdFromRequest(HttpServletRequest request) {

        String userAgent = request.getHeader(USER_AGENT_HEADER);

        if (userAgent == null || userAgent.isEmpty()) {
            throw new AuthenticationException(ErrorCode.MISSING_USER_AGENT);
        }

        return UUID.nameUUIDFromBytes(userAgent.getBytes()).toString();
    }

}
