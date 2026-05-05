package com.swapper.monolith.exception.enums;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public enum ApiResponses {
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),
    DUPLICATED_RESOURCE(HttpStatus.CONFLICT,"This Resource Already Exists"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource Not Found"),
    CREATED(HttpStatus.CREATED, "Created Successfully"),
    OK(HttpStatus.OK, "Retrieved Successfully"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh token has expired, please log in again"),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Invalid refresh token"),
    PASSWORD_RESET_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Password reset link has expired, please request a new one"),
    PASSWORD_RESET_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Invalid password reset token"),
    LISTING_ACTIVE_TRANSACTION(HttpStatus.CONFLICT, "Cannot delete a listing with an active transaction in progress"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "You do not have permission to perform this action"),
    ;
    HttpStatus httpStatus;
    String message;

    ApiResponses(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
    public String getMessage() {
        return this.message;
    }
}
