package com.swapper.monolith.exception.CustomExceptions;

import com.swapper.monolith.exception.enums.ApiResponses;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ForbiddenException extends RuntimeException {
    private final HttpStatus httpStatus = HttpStatus.FORBIDDEN;

    public ForbiddenException() {
        super(ApiResponses.FORBIDDEN.getMessage());
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
