package com.bankbazaar.kafka.dto.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class DataDto {
    private Long id;
    @NotNull
    private String fileName;
    @NotNull
    private String headers;
    @NotNull
    private String[] data;
}
