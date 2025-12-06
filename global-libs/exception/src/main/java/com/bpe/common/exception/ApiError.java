package com.bpe.common.exception;

import java.time.LocalDateTime;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        String traceId,
        Object details
) {

    // Optional convenience constructor (without timestamp)
    public ApiError(
            int status,
            String error,
            String code,
            String message,
            String path,
            String traceId,
            Object details
    ) {
        this(
                LocalDateTime.now(), // auto-generate timestamp
                status,
                error,
                code,
                message,
                path,
                traceId,
                details // must forward details
        );
    }
}
