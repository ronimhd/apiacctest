package com.ronim.test.api.service;

import com.ronim.test.api.dto.AddUserDTO;
import com.ronim.test.api.dto.UpdateUserDTO;
import com.ronim.test.api.entity.User;

public interface UserService {
    String addUser(AddUserDTO dto);
    User getUser(String ssn);
    User updateUser(UpdateUserDTO dto);
    void deleteUser(String ssn);
}
