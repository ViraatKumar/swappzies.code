package com.swapper.monolith.security.SecurityConfiguration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "security")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityConfiguration {
    String secretKey;
    long expiration;
}
