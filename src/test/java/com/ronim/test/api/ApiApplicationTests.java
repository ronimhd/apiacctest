package com.ronim.test.api;

import com.ronim.test.api.dto.AddUserDTO;
import com.ronim.test.api.entity.User;
import com.ronim.test.api.exception.BadRequestException;
import com.ronim.test.api.exception.NotFoundException;
import com.ronim.test.api.service.UserService;
import com.ronim.test.api.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Rollback
class ApiApplicationTests {

	@Autowired
	UserService userService;

	@Test
	void addUserValidation() {
		AddUserDTO dto = AddUserDTO.builder()
				.build();
		try {
			userService.addUser(dto);
			fail("should throw BadRequestException");
		} catch (Exception e) {
			assertTrue(e instanceof BadRequestException);
			var bex = (BadRequestException) e;
			assertEquals(UserServiceImpl.USER_NAME_FIELD, bex.getField());
		}

		dto = AddUserDTO.builder().username("ausername")
				.build();

		try {
			userService.addUser(dto);
			fail("should throw BadRequestException");
		} catch (Exception e) {
			assertTrue(e instanceof BadRequestException);
			var bex = (BadRequestException) e;
			assertEquals(UserServiceImpl.USER_SSN_FIELD, bex.getField());
		}

		dto = AddUserDTO.builder().username("ausername")
				.socialSecurityNumber("abc")
				.build();

		try {
			userService.addUser(dto);
			fail("should throw BadRequestException");
		} catch (Exception e) {
			assertTrue(e instanceof BadRequestException);
			var bex = (BadRequestException) e;
			assertEquals(UserServiceImpl.USER_SSN_FIELD, bex.getField());
		}

		dto = AddUserDTO.builder().username("ausername")
				.socialSecurityNumber("123456")
				.build();

		try {
			userService.addUser(dto);
			fail("should throw BadRequestException");
		} catch (Exception e) {
			assertTrue(e instanceof BadRequestException);
			var bex = (BadRequestException) e;
			assertEquals(UserServiceImpl.USER_SSN_FIELD, bex.getField());
		}

		dto = AddUserDTO.builder().username("ausername")
				.socialSecurityNumber("123")
				.build();

		try {
			userService.addUser(dto);
			fail("should throw BadRequestException");
		} catch (Exception e) {
			assertTrue(e instanceof BadRequestException);
			var bex = (BadRequestException) e;
			assertEquals(UserServiceImpl.USER_DOB_FIELD, bex.getField());
		}

		dto = AddUserDTO.builder().username("ausername")
				.socialSecurityNumber("1234")
				.dateOfBirth("01-01-2000")
				.build();

		try {
			userService.addUser(dto);
			fail("should throw BadRequestException");
		} catch (Exception e) {
			assertTrue(e instanceof BadRequestException);
			var bex = (BadRequestException) e;
			assertEquals(UserServiceImpl.USER_DOB_FIELD, bex.getField());
		}

		dto = AddUserDTO.builder().username("ausername")
				.socialSecurityNumber("1234")
				.dateOfBirth("2000-01-01")
				.createdBy("abc123@#")
				.build();

		try {
			userService.addUser(dto);
			fail("should throw BadRequestException");
		} catch (Exception e) {
			assertTrue(e instanceof BadRequestException);
			var bex = (BadRequestException) e;
			assertEquals(UserServiceImpl.CREATEDBY_FIELD, bex.getField());
		}

		dto = AddUserDTO.builder().username("ausername")
				.socialSecurityNumber("1234")
				.dateOfBirth("2000-01-01")
				.build();

		try {
			String ssn = userService.addUser(dto);
			assertEquals("01234", ssn);
		} catch (Exception e) {
			fail("should not get exception ", e);
		}

		User user = userService.getUser("01234");
		assertNotNull(user);
		assertEquals(UserServiceImpl.SPRING_BOOT_TEST, user.getCreatedBy());
		assertNotNull(user.getCreatedAt());

		user = userService.getUser("1234");
		assertNotNull(user);
		assertEquals(UserServiceImpl.SPRING_BOOT_TEST, user.getCreatedBy());
		assertNotNull(user.getCreatedAt());

		dto = AddUserDTO.builder().username("ausername")
				.socialSecurityNumber("12345")
				.dateOfBirth("2000-01-01")
				.createdBy("abc123")
				.build();

		try {
			String ssn = userService.addUser(dto);
			assertEquals("12345", ssn);
			user = userService.getUser("12345");
			assertEquals("abc123", user.getCreatedBy());
		} catch (Exception e) {
			fail("should not get exception ", e);
		}

		try {
			user = userService.getUser("00001");
			fail("should throw exception");
		} catch (Exception e) {
			assertTrue(e instanceof NotFoundException);
			var ex = (NotFoundException) e;
			assertEquals("00001", ex.getValue());
		}
	}

}
