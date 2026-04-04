package com.swapper.monolith.exception;

import com.swapper.monolith.dto.ErrorResponse;
import com.swapper.monolith.exception.CustomExceptions.DuplicatedResourceException;
import com.swapper.monolith.exception.CustomExceptions.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final String exceptionString = "Exception caught in GlobalExceptionHandler - {}";
    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> handleException(Exception e, HttpServletRequest request){
        LOGGER.error(exceptionString, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.errorBuilder()
                .message(e.getMessage())
                .path(request.getServletPath())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build()
        );
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .findFirst()
                .orElse(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.errorBuilder()
                .message(message)
                .path(request.getServletPath())
                .status(HttpStatus.BAD_REQUEST)
                .build());
    }

    @ExceptionHandler(DuplicatedResourceException.class)
    public ResponseEntity<?> duplicateResourceException(DuplicatedResourceException e, HttpServletRequest request){
        LOGGER.error(exceptionString,e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(ErrorResponse.errorBuilder()
                .message(e.getMessage())
                .status(e.getHttpStatus())
                .path(request.getServletPath())
                .build());
    }
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> tokenExpiredException(TokenExpiredException e, HttpServletRequest request) {
        LOGGER.error(exceptionString, e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(ErrorResponse.errorBuilder()
                .message(e.getMessage())
                .status(e.getHttpStatus())
                .path(request.getServletPath())
                .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentialsException(BadCredentialsException e, HttpServletRequest request){
        LOGGER.error(exceptionString,e.getMessage());
        return ResponseEntity.status(401).body(ErrorResponse.errorBuilder()
                        .message(e.getMessage())
                        .status(HttpStatus.UNAUTHORIZED)
                        .path(request.getServletPath())
                .build());
    }
}
