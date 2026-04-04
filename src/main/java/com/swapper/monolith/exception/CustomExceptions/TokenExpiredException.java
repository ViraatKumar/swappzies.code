package com.swapper.monolith.exception.CustomExceptions;

import com.swapper.monolith.exception.enums.ApiResponses;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenExpiredException extends RuntimeException {
    private final String message;
    private final HttpStatus httpStatus;

    public TokenExpiredException(ApiResponses error) {
        super(error.getMessage());
        this.message = error.getMessage();
        this.httpStatus = error.getHttpStatus();
    }
}
