package com.bankbazaar.kafka.core.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "file_status")
public class FileStatusEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="file_name",nullable = false)
    private String fileName;

    @Column(name = "status", nullable = false)
    private Status status;

    @CreatedDate
    @Column(name = "created_date",updatable = false)
    private Date createdDate;

    @LastModifiedDate
    @Column(name = "modified_date",updatable = false)
    private Date lastModifiedDate;
}
