package com.bankbazaar.kafka.service.service;

import com.bankbazaar.kafka.core.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisUtil {
    @Autowired
    private RedisTemplate redisTemplate;

    public void saveToRedis(Long id, Status status)
    {
        redisTemplate.opsForValue().set(id, status, 10, TimeUnit.SECONDS);
    }

    public Status getFromRedis(Long id)
    {
        return (Status) redisTemplate.opsForValue().get(id);
    }
}
