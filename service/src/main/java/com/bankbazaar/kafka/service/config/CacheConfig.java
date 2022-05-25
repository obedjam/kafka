package com.bankbazaar.kafka.service.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
    @Value("${fileStatus.cache.expiry.inSeconds}")
    private Long timeToLive;
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("StatusCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(timeToLive)));
    }
}
