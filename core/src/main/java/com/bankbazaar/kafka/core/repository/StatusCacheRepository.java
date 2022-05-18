package com.bankbazaar.kafka.core.repository;

import com.bankbazaar.kafka.core.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StatusCacheRepository implements StatusCacheDao {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String KEY = "Status";

    @Override
    public boolean saveStatus(Long id, Status status) {
        try {
            redisTemplate.opsForHash().put(KEY, id, status);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Status getStatusById(Long id) {
        Status status = (Status) redisTemplate.opsForHash().get(KEY,id);
        return status;
    }

}
