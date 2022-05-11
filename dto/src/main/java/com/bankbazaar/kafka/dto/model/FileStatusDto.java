package com.bankbazaar.kafka.dto.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class FileStatusDto {
    private Long id;
    private String fileName;
    @NotNull
    private String status;
}
