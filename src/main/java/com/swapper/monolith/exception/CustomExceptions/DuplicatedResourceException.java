package com.swapper.monolith.exception.CustomExceptions;

import com.swapper.monolith.exception.enums.ApiResponses;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicatedResourceException extends RuntimeException {
    private final String message;
    private final ApiResponses error;
    private final HttpStatus httpStatus;

    public DuplicatedResourceException(ApiResponses error) {
        super(error.getMessage());
        this.message = error.getMessage();
        this.error = error;
        this.httpStatus = error.getHttpStatus();
    }

}
