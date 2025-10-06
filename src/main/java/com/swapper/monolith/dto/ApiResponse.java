package com.swapper.monolith.dto;

import com.swapper.monolith.exception.enums.ApiResponses;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Builder
public record ApiResponse<T>(T payload, String message, HttpStatus status) {

}
