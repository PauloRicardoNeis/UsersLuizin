package com.usersluizin.userservice.exception;

import java.time.OffsetDateTime;

public class ApiError {
    private final OffsetDateTime timestamp = OffsetDateTime.now();
    private final String message;

    public ApiError(String message) {
        this.message = message;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
