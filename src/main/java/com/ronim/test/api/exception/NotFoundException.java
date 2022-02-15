package com.ronim.test.api.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotFoundException extends BusinessException{
    private String value;
}
