package com.swapper.monolith.external.twitch;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="twitch-api")
@FieldDefaults(level= AccessLevel.PRIVATE)
@Data
 public class TwitchConfiguration {
    String clientId;
    String clientSecret;
    String grantType;
    String accessTokenBaseUrl;
}
