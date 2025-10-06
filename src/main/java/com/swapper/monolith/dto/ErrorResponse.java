package com.swapper.monolith.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {
    String message;
    String path;
    HttpStatus status;
    String timestamp;

    @Builder(builderMethodName="errorBuilder")
    public static ErrorResponse getErrorResponse(String message,String path,HttpStatus status){
        String timestamp = LocalDateTime.now().toString();
        return new ErrorResponse(message,path,status,timestamp);
    }
}
