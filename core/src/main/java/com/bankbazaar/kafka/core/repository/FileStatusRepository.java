package com.bankbazaar.kafka.core.repository;

import com.bankbazaar.kafka.core.model.FileStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileStatusRepository extends JpaRepository<FileStatusEntity, Long> {
    public FileStatusEntity findByFileName(String name);
}