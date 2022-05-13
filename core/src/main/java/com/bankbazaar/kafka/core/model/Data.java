package com.bankbazaar.kafka.core.model;

import lombok.NoArgsConstructor;

import java.util.List;
@lombok.Data
@NoArgsConstructor
public class Data {
    private Long id;
    private String fileName;
    private String[] headers;
    private List<String[]> data;
}
