package com.bankbazaar.kafka.service.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private Long executionId;
    private String status;
    private Date timeStamp;
}
