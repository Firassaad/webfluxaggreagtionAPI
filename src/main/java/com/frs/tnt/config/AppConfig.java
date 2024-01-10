package com.frs.tnt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${myapp.base.url}")
    private static String baseUrl;

    public static String getBaseUrl() {
        return baseUrl;
    }
}
