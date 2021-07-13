package com.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.user.model.User;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repository;

	private User userOne;
	private User userTwo;

	@BeforeEach
	void setUp() {
		userOne = new User(0, "test@gmail.com", "test", null);
		userTwo = new User(0, "two@gmail.com", "two", null);
		repository.save(userTwo);
	}

	@AfterEach
	void tearDown() {
		userOne = null;
	}

	@Test
	public void givenEmailWhenUserExistsThenReturnTrue() {

		boolean exists = repository.existsByEmail("sara@allstate.com"); // sara@allstate.com exists in my Local SQL DB

		assertTrue(exists);
	}

	@Test
	public void givenEmailWhenUserDoesNotExistsThenReturnFalse() {

		boolean exists = repository.existsByEmail("xyz@allstate.com");

		assertFalse(exists);
	}

	@Test
	public void givenEmailWhenUserExistsThenReturnOptionalWithUser() {

		Optional<User> optionalUser = repository.findByEmailOrUserId("two@gmail.com", null);	//As we did repository.save(userTwo) in setUp
		assertTrue(optionalUser.isPresent(), "For Existing User, method should return optional with user");
		User user = optionalUser.get();
		assertEquals("two", user.getPassword());
	}

}
