package com.ronim.test.api.service;

import com.ronim.test.api.dto.AddUserDTO;
import com.ronim.test.api.entity.User;

public interface UserService {
    String addUser(AddUserDTO user);
    User getUser(String id);
}
