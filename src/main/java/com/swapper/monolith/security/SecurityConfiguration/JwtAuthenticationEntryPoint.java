package com.swapper.monolith.security.SecurityConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swapper.monolith.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String message = (String) request.getAttribute("auth_error_message");
        if (message == null) {
            message = "Authentication is required to access this resource";
        }

        ErrorResponse body = ErrorResponse.errorBuilder()
                .message(message)
                .path(request.getServletPath())
                .status(HttpStatus.UNAUTHORIZED)
                .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
