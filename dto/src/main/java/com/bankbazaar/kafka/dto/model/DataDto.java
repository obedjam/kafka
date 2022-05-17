package com.bankbazaar.kafka.dto.model;

import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@lombok.Data
@NoArgsConstructor
public class DataDto {
    private Long id;
    @NotNull
    @NotBlank
    private String fileName;

    @NotNull
    @NotBlank
    private String headers;

    @NotNull
    @NotEmpty
    private String[] data;
}
