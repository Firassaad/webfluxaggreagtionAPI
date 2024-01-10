package com.frs.tnt.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.common.ratelimiter.configuration.RateLimiterConfigCustomizer;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class RateLimiterConfiguration {

    // @Bean
    // public RateLimiterRegistry rateLimiterRegistry() {
    //     return RateLimiterRegistry.ofDefaults();
    // }

    // @Bean
    // public RateLimiter rateLimitWithCustomConfig(RateLimiterRegistry rateLimiterRegistry) {
    //     RateLimiterConfig customConfig = RateLimiterConfig.custom()
    //             .limitForPeriod(2)
    //             .limitRefreshPeriod(Duration.of(10, ChronoUnit.SECONDS))
    //             .timeoutDuration(Duration.of(5, ChronoUnit.SECONDS))
    //             .build();

    //     return rateLimiterRegistry.rateLimiter("customRateLimiterConfig", customConfig);
    // }

    // @Bean
    // public RateLimiterConfigCustomizer rateLimiterConfigCustomizer() {
    //     return RateLimiterConfigCustomizer.ofDefaults(null);
    // }


}
