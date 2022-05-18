package com.bankbazaar.kafka.core.repository;

import com.bankbazaar.kafka.core.model.Status;

public interface StatusCacheDao {
    boolean saveStatus(Long id, Status status);
    Status getStatusById(Long id);
}
