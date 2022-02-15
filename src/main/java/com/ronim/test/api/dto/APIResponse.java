package com.ronim.test.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;


@Data
@Builder
public class APIResponse {
    private String status;
    private int code;
    @JsonIgnore
    private int httpStatus;
    private String message;
    
}
