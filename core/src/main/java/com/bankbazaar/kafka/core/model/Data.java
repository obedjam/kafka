package com.bankbazaar.kafka.core.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@lombok.Data
@NoArgsConstructor
public class Data implements Serializable{
    private static final long serialVersionUID = -6364892190960198090L;
    private Long id;
    private String fileName;
    private String[] headers;
    private List<String[]> data;
}
