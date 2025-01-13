/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fangjk.hsbcdemo1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 *
 * @author fangj
 */
@Configuration
@EnableCaching // Enable caching feature
public class RedisConfig {
    
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;
    
    @Value("${spring.redis.ssl:}")
    private boolean ssl;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() throws Exception {
        RedisStandaloneConfiguration  redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        if (!redisPassword.isEmpty()) {
            redisConfig.setPassword(RedisPassword.of(redisPassword));
        }
        
        final LettuceClientConfiguration.LettuceClientConfigurationBuilder luttuceClientConfigurationBuilder =
                LettuceClientConfiguration.builder();
        if(this.ssl) {
            luttuceClientConfigurationBuilder.useSsl().disablePeerVerification();
        }
        final LettuceClientConfiguration luttuceClientConfiguration = luttuceClientConfigurationBuilder.build();
        
        return new LettuceConnectionFactory(redisConfig,luttuceClientConfiguration);
    }

    // RedisTemplate for interacting with Redis
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // Set key serializer to String
        template.setKeySerializer(new StringRedisSerializer());
        // Set value serializer to default Jackson
        //template.setValueSerializer(new org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        return template;
    }

    // Cache manager for Redis, used for managing cache configurations
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.create(connectionFactory);
    }
}