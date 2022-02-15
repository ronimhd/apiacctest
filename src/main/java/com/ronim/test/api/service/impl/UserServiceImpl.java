package com.ronim.test.api.service.impl;

import com.ronim.test.api.dto.AddUserDTO;
import com.ronim.test.api.dto.UpdateUserDTO;
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

        String name = validateAndExtractNameField(dto.getUsername());
        String ssn = validateAndExtractSsn(dto.getSocialSecurityNumber());
        LocalDate dob = validateAndExtractDateOfBirth(dto.getDateOfBirth());
        String createdBy = validateAndConstructTedBy(dto.getCreatedBy());

        User user = User.builder()
                .username(name)
                .socialSecurityNumber(ssn)
                .dateOfBirth(dob)
                .deleted(false)
                .createdAt(LocalDate.now())
                .createdBy(createdBy)
                .build();

        this.userRepository.save(user);

        return user.getSocialSecurityNumber();
    }

    private LocalDate validateAndExtractDateOfBirth(String dobStr) {
        if(dobStr == null) {
            throw BadRequestException.builder()
                    .field(USER_DOB_FIELD)
                    .value(dobStr)
                    .build();
        }

        try {
            LocalDate dob = LocalDate.parse(dobStr, DateTimeFormatter.ISO_DATE);
            return dob;
        } catch (Exception e) {
            throw BadRequestException.builder()
                    .field(USER_DOB_FIELD)
                    .value(dobStr)
                    .build();
        }
    }

    private String validateAndExtractSsn(String ssn) {
        if( ssn == null ) {
            throw BadRequestException.builder()
                    .field(USER_SSN_FIELD)
                    .value("empty")
                    .build();
        }

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
            var u = user.get();
            if(!u.getDeleted()) {
                throw SsnConflictException.builder()
                        .value(ssn)
                        .build();
            }
        }

        return ssn;
    }

    private String validateAndExtractNameField(String username) {
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

    private String validateAndConstructTedBy(String createdBy) {

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
        User user =  this.userRepository.findById(ssn).orElseThrow(
                () -> NotFoundException.builder()
                        .value(finalSsn)
                        .build()
        );

        if(user.getDeleted()) {
            throw NotFoundException.builder()
                    .value(finalSsn)
                    .build();
        }

        return user;
    }

    @Override
    public User updateUser(UpdateUserDTO dto) {
        String ssn = StringUtils.leftPad(dto.getSocialSecurityNumber(), SSN_LENGTH, PAD_SSN);
        Optional<User> opUser = userRepository.findById(ssn);
        if(opUser.isEmpty() || opUser.get().getDeleted()) {
            throw NotFoundException.builder()
                    .value(dto.getSocialSecurityNumber())
                    .build();
        }
        String name = validateAndExtractNameField(dto.getUsername());
        LocalDate dob = validateAndExtractDateOfBirth(dto.getDateOfBirth());
        String updatedBy = validateAndConstructTedBy(dto.getUpdatedBy());

        User user = opUser.get();
        user.setUsername(name);
        user.setDateOfBirth(dob);
        user.setUpdatedAt(LocalDate.now());
        user.setUpdateBy(updatedBy);
        this.userRepository.save(user);

        return user;
    }

    @Override
    public void deleteUser(String ssn) {
        String lssn = StringUtils.leftPad(ssn, SSN_LENGTH, PAD_SSN);
        Optional<User> opUser = userRepository.findById(lssn);
        if(opUser.isEmpty() || opUser.get().getDeleted()) {
            throw NotFoundException.builder()
                    .value(ssn)
                    .build();
        }
        User user = opUser.get();
        user.setDeleted(true);
        this.userRepository.save(user);
    }
}
