package com.gesper.server.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        boolean success,
        int status,
        String message,
        Map<String, String> errors,
        String path,
        Instant timestamp
) {
    public static ErrorResponse of(int status, String message, String path) {
        return new ErrorResponse(false, status, message, null, path, Instant.now());
    }

    public static ErrorResponse of(int status, String message, Map<String, String> errors, String path) {
        return new ErrorResponse(false, status, message, errors, path, Instant.now());
    }
}
