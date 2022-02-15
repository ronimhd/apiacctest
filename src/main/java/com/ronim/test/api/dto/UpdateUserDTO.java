package com.ronim.test.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserDTO {

    @JsonIgnore
    private String socialSecurityNumber;
    private String username;
    private String dateOfBirth;
    private String updatedBy;
}
