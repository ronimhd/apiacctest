package com.ronim.test.api.dto;

import com.ronim.test.api.exception.BadRequestException;
import com.ronim.test.api.exception.BusinessException;
import com.ronim.test.api.exception.NotFoundException;
import com.ronim.test.api.exception.SsnConflictException;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class APIResponseErrorBuilder {

    private Map<Class,Builder > builderMap = new HashMap<>();

    private static interface Builder {
        APIResponse convert(Exception exception);
    }

    public APIResponseErrorBuilder() {
        Builder forBadRequest = new Builder() {
            @Override
            public APIResponse convert(Exception exception) {
                BadRequestException exp = (BadRequestException) exception;

                return APIResponse.builder().status("BAD_REQUEST")
                        .httpStatus(HttpStatus.BAD_REQUEST.value())
                        .code(3001)
                        .message("Invalid value for field " + exp.getField() + ", " +
                                "rejected value: " + exp.getValue())
                        .build();
            }
        };
        this.builderMap.put(BadRequestException.class,forBadRequest);

        Builder forConflict = new Builder() {
            @Override
            public APIResponse convert(Exception exception) {
                SsnConflictException exp = (SsnConflictException) exception;

                return APIResponse.builder().status("CONFLICT")
                        .code(3002)
                        .message("Record with SSN " + exp.getValue() + " already exists in the system")
                        .build();
            }
        };
        this.builderMap.put(SsnConflictException.class,forConflict);
        Builder forNotFound = new Builder() {
            @Override
            public APIResponse convert(Exception exception) {
                NotFoundException exp = (NotFoundException) exception;

                return APIResponse.builder().status("NOT FOUND")
                        .httpStatus(HttpStatus.NOT_FOUND.value())
                        .code(3000)
                        .message("No such resource with id " + exp.getValue())
                        .build();
            }
        };
        this.builderMap.put(NotFoundException.class,forNotFound);
    }

    public APIResponse constructFrom(Exception exception) {
        Builder builder = this.builderMap.get(exception.getClass());
        if(builder == null) {
            return APIResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .status("internal error")
                    .message("internal error")
                    .build();
        }
        return builder.convert(exception);
    }

}
