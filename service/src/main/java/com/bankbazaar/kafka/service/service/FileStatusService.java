package com.bankbazaar.kafka.service.service;

import com.bankbazaar.kafka.core.manager.FileStatusManager;
import com.bankbazaar.kafka.dto.model.FileStatusDto;
import com.bankbazaar.kafka.service.mapper.FileStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileStatusService {
    @Autowired
    private FileStatusManager fileStatusManager;
    @Autowired
    private FileStatusMapper mapper;

    public FileStatusDto insert(FileStatusDto data)
    {
        return mapper.DomainToDto(fileStatusManager.insert(mapper.dtoToDomain(data)));
    }

    public FileStatusDto update(FileStatusDto data)
    {
        return mapper.DomainToDto(fileStatusManager.update(mapper.dtoToDomain(data)));
    }
}