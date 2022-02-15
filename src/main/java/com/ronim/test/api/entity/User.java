package com.ronim.test.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String socialSecurityNumber;
    private String username;
    private LocalDate dateOfBirth;
    private LocalDate createdAt;
    private String createdBy;
    private LocalDate updatedAt;
    private String updateBy;
    private Boolean deleted;
}
