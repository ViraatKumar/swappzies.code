package com.swapper.monolith.exception.enums;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public enum ApiResponses {
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),
    USER_DUPLICATED_ERROR(HttpStatus.CONFLICT,"This User Already Exists"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource Not Found"),
    CREATED(HttpStatus.CREATED, "Created Successfully"),
    OK(HttpStatus.OK, "Retrieved Successfully"),
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
