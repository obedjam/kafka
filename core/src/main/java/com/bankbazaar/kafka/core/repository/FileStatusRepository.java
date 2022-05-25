package com.bankbazaar.kafka.core.repository;

import com.bankbazaar.kafka.core.model.FileStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileStatusRepository extends JpaRepository<FileStatusEntity, Long> {
    public Optional<FileStatusEntity> findByFileName(String name);
}