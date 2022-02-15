package com.ronim.test.api.dto;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Builder
public class AddUserDTO {

    private String socialSecurityNumber;
    private String username;
    private String dateOfBirth;
    private String createdBy;
}
