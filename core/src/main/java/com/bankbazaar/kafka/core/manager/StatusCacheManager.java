package com.bankbazaar.kafka.core.manager;

import com.bankbazaar.kafka.core.model.Status;
import com.bankbazaar.kafka.core.repository.StatusCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusCacheManager {
    @Autowired
    private StatusCacheRepository statusCacheRepository;

    public boolean saveStatus(Long id, Status status)
    {
        return statusCacheRepository.saveStatus(id, status);
    }

    public Status getStatusById(Long id)
    {
        return statusCacheRepository.getStatusById(id);
    }
}
