package com.ronim.test.api.service.impl;

import com.ronim.test.api.dto.AddUserDTO;
import com.ronim.test.api.entity.User;
import com.ronim.test.api.exception.BadRequestException;
import com.ronim.test.api.exception.NotFoundException;
import com.ronim.test.api.exception.SsnConflictException;
import com.ronim.test.api.repository.UserRepository;
import com.ronim.test.api.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    public static final String USER_NAME_FIELD = "name";
    public static final String CREATEDBY_FIELD = "createdBy";
    public static final String UPDATEBY_FIELD = "updatedBy";
    public static final String USER_DOB_FIELD = "dateOfBirth";
    public static final String USER_SSN_FIELD = "social security number";
    public static final String SPRING_BOOT_TEST = "SPRING_BOOT_TEST";
    public static final String PAD_SSN = "0";
    public static final int SSN_LENGTH = 5;
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String addUser(AddUserDTO dto) {

        String name = validateNameField(dto);
        String ssn = validateAndExtractSsn(dto);
        LocalDate dob = validateDateOfBirth(dto);
        String createdBy = validateAndConstructCreatedBy(dto);

        User user = User.builder()
                .username(name)
                .socialSecurityNumber(ssn)
                .dateOfBirth(dob)
                .createdAt(LocalDate.now())
                .createdBy(createdBy)
                .build();

        this.userRepository.save(user);

        return user.getSocialSecurityNumber();
    }

    private LocalDate validateDateOfBirth(AddUserDTO dto) {
        if(dto.getDateOfBirth() == null) {
            throw BadRequestException.builder()
                    .field(USER_DOB_FIELD)
                    .value(dto.getDateOfBirth())
                    .build();
        }

        try {
            LocalDate dob = LocalDate.parse(dto.getDateOfBirth(), DateTimeFormatter.ISO_DATE);
            return dob;
        } catch (Exception e) {
            throw BadRequestException.builder()
                    .field(USER_DOB_FIELD)
                    .value(dto.getDateOfBirth())
                    .build();
        }
    }

    private String validateAndExtractSsn(AddUserDTO dto) {
        if( dto.getSocialSecurityNumber() == null ) {
            throw BadRequestException.builder()
                    .field(USER_SSN_FIELD)
                    .value("empty")
                    .build();
        }
        String ssn = dto.getSocialSecurityNumber();
        if(!StringUtils.isNumeric(ssn)) {
            throw BadRequestException.builder()
                    .field(USER_SSN_FIELD)
                    .value(ssn)
                    .build();
        }

        if(ssn.length() > SSN_LENGTH) {
            throw BadRequestException.builder()
                    .field(USER_SSN_FIELD)
                    .value(ssn)
                    .build();
        }
        ssn = StringUtils.leftPad(ssn, SSN_LENGTH, PAD_SSN);
        Optional<User> user = this.userRepository.findById(ssn);

        if(user.isPresent()) {
            throw SsnConflictException.builder()
                    .value(ssn)
                    .build();
        }

        return ssn;
    }

    private String validateNameField(AddUserDTO dto) {
        String username = dto.getUsername();
        if (StringUtils.isEmpty(username)) {
            throw BadRequestException.builder()
                    .field(USER_NAME_FIELD)
                    .value("empty")
                    .build();
        }
        if (username.length() < 2 || username.length() > 50) {
            throw BadRequestException.builder()
                    .field(USER_NAME_FIELD)
                    .value(username)
                    .build();
        }
        if (!StringUtils.isAlphanumeric(username)) {
            throw BadRequestException.builder()
                    .field(USER_NAME_FIELD)
                    .value(username)
                    .build();
        }
        return username;
    }

    private String validateAndConstructCreatedBy(AddUserDTO dto) {
        String createdBy = dto.getCreatedBy();
        if (StringUtils.isEmpty(createdBy)) {
            return SPRING_BOOT_TEST;
        }
        if (!StringUtils.isAlphanumeric(createdBy)) {
            throw BadRequestException.builder()
                    .field(CREATEDBY_FIELD)
                    .value(createdBy)
                    .build();
        }
        return createdBy;
    }

    @Override
    public User getUser(String ssn) {
        ssn = StringUtils.leftPad(ssn, SSN_LENGTH, PAD_SSN);
        String finalSsn = ssn;
        return this.userRepository.findById(ssn).orElseThrow(
                () -> NotFoundException.builder()
                        .value(finalSsn)
                        .build()
        );
    }
}
