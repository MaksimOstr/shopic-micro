package com.authservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "refresh-toke")
public class RefreshTokenProperties {
    private String secret;
    private Integer expiresAt;
    private String cookieName;
}
