package com.swapper.monolith.exception;

import com.swapper.monolith.dto.ErrorResponse;
import com.swapper.monolith.exception.CustomExceptions.DuplicatedResourceException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> handleException(Exception e, HttpServletRequest request){
        LoggerFactory.getLogger(GlobalExceptionHandler.class).error("Exception caught in GlobalExceptionHandler - "+e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.errorBuilder()
                .message(e.getMessage())
                .path(request.getServletPath())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build()
        );
    }
    @ExceptionHandler(DuplicatedResourceException.class)
    public ResponseEntity<?> duplicateResourceException(DuplicatedResourceException e, HttpServletRequest request){
        LoggerFactory.getLogger(GlobalExceptionHandler.class).error("Exception caught in GlobalExceptionHandler - "+e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(ErrorResponse.errorBuilder()
                .message(e.getMessage())
                .status(e.getHttpStatus())
                .path(request.getServletPath())
                .build());
    }
}
