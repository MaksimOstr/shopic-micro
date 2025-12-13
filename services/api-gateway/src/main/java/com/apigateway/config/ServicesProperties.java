package com.apigateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "services")
@Getter
@Setter
public class ServicesProperties {
    private String authUrl;
    private String productUrl;
    private String cartUrl;
    private String orderUrl;
    private String paymentUrl;
}
