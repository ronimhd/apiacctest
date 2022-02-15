package com.ronim.test.api.infrastructure;

import com.ronim.test.api.dto.APIResponse;
import com.ronim.test.api.dto.APIResponseErrorBuilder;
import com.ronim.test.api.dto.AddUserDTO;
import com.ronim.test.api.entity.User;
import com.ronim.test.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;
    private final APIResponseErrorBuilder apiResponseErrorBuilder = new APIResponseErrorBuilder();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<Object> addUser(@RequestBody AddUserDTO user) {
        try {
            String userId = userService.addUser(user);
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            APIResponse apiResponse = this.apiResponseErrorBuilder.constructFrom(e) ;
            return ResponseEntity.status(apiResponse.getHttpStatus()).body(apiResponse);
        }
    }
}
