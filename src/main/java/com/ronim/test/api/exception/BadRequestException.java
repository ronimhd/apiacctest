package com.ronim.test.api.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BadRequestException extends BusinessException{
    private String field;
    private String value;

}
