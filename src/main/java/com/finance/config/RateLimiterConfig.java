package com.finance.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfig {

    // Global bucket for public endpoints or auth to prevent brute-force attacks
    // For simplicity without caching mechanism per IP, using a generic configuration bucket.
    // In production, we'd use a CacheManager or Redis mapping IP -> Bucket
    @Bean
    public Bucket defaultRateLimitBucket() {
        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
