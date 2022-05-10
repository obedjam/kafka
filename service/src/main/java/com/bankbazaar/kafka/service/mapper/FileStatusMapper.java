package com.bankbazaar.kafka.service.mapper;

import com.bankbazaar.kafka.core.model.FileStatusEntity;
import com.bankbazaar.kafka.dto.model.FileStatusDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileStatusMapper {
    FileStatusEntity dtoToDomain(FileStatusDto dto);
    FileStatusDto DomainToDto(FileStatusEntity entity);
}
