package com.bankbazaar.kafka.service.service;

import com.bankbazaar.kafka.core.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisUtil {
    @Value("${fileStatus.cache.expiry.inSeconds}")
    private Long timeToLive;
    @Autowired
    private RedisTemplate redisTemplate;

    public void saveToRedis(Long id, Status status)
    {
        redisTemplate.opsForValue().set(id, status, timeToLive, TimeUnit.SECONDS);
    }

    public Status getFromRedis(Long id)
    {
        return (Status) redisTemplate.opsForValue().get(id);
    }
}
