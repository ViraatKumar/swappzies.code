package com.swapper.monolith.security.SecurityConfiguration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "security")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityConfiguration {
    String secretKey;
    long expiration;
    long refreshTokenExpiration;
    Cors cors = new Cors();

    @Data
    public static class Cors {
        List<String> allowedOrigins = List.of();
    }
}
