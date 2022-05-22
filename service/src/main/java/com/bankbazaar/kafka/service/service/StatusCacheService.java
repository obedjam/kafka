package com.bankbazaar.kafka.service.service;

import com.bankbazaar.kafka.core.manager.StatusCacheManager;
import com.bankbazaar.kafka.core.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusCacheService {
    @Autowired
    private StatusCacheManager statusCacheManager;

    public boolean saveStatus(Long id, Status status)
    {
        return statusCacheManager.saveStatus(id, status);
    }

    public Status getStatusById(Long id)
    {
        return statusCacheManager.getStatusById(id);
    }
}
