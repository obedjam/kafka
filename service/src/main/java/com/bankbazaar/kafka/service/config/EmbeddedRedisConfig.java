package com.bankbazaar.kafka.service.config;

import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.SocketUtils;
import redis.embedded.RedisServer;

@Configuration
//@Profile({"dev","test"})
public class EmbeddedRedisConfig {



    private static final String REDIS_HOST = "localhost";
    private final int redisPort = SocketUtils.findAvailableTcpPort();



    private final RedisServer redisServer;



    public EmbeddedRedisConfig() {
        this.redisServer = new RedisServer(redisPort);
    }



    /**
     * Start the embedded redis server.
     */
    @PostConstruct
    public void postConstruct() {
        if (!this.redisServer.isActive()) {
            this.redisServer.start();
        }
    }



    @PreDestroy
    public void preDestroy() {
        this.redisServer.stop();
    }



    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(REDIS_HOST, redisPort);
    }



    /**
     * Constructs the RedisTemplate bean.
     *
     * @param connectionFactory LettuceConnectionFactory to hook to embedded Redis server
     * @return RedisTemplate
     */
    @SuppressWarnings("rawtypes")
    @Bean
    public RedisTemplate redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<Long, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
