package com.peatroxd.bulletinboardproject.common.exception;

public record ApiErrorResponse(
        int status,
        String error,
        String message
) {
}
