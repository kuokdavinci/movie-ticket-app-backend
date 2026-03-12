package com.example.MovieTicket.Config;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                );
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration(
                        "moviesList",
                        redisCacheConfiguration().entryTtl(Duration.ofMinutes(10))
                )
                .withCacheConfiguration(
                        "moviesById",
                        redisCacheConfiguration().entryTtl(Duration.ofMinutes(10))
                )
                .withCacheConfiguration(
                        "moviesSearch",
                        redisCacheConfiguration().entryTtl(Duration.ofMinutes(5))
                );
    }
}
