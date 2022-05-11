package com.bankbazaar.kafka.dto.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class FileStatusDto {
    private Long id;
    private String fileName;
    @NotNull
    private String status;
}
