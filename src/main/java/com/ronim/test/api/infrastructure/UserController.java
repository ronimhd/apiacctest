package com.ronim.test.api.infrastructure;

import com.ronim.test.api.dto.APIResponse;
import com.ronim.test.api.dto.APIResponseErrorBuilder;
import com.ronim.test.api.dto.AddUserDTO;
import com.ronim.test.api.dto.UpdateUserDTO;
import com.ronim.test.api.entity.User;
import com.ronim.test.api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            return constructObjectResponseEntityFromException(e);
        }
    }

    private ResponseEntity<Object> constructObjectResponseEntityFromException(Exception e) {
        APIResponse apiResponse = this.apiResponseErrorBuilder.constructFrom(e);
        return ResponseEntity.status(apiResponse.getHttpStatus()).body(apiResponse);
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> updateUser(@PathVariable String id, @RequestBody UpdateUserDTO dto) {
        try {
            dto.setSocialSecurityNumber(id);
            User user = userService.updateUser(dto);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return constructObjectResponseEntityFromException(e);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUser(@PathVariable String id) {
        try {
            User user = userService.getUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return constructObjectResponseEntityFromException(e);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return constructObjectResponseEntityFromException(e);
        }
    }


}
