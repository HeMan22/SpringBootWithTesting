package com.user.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.web.server.ResponseStatusException;

import com.user.exception.UserExistsException;
import com.user.model.User;
import com.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)

@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserServiceImplTest {

	private static final String EMAILONE = "one@gmail.com";
	private User savedUserOne;
	private User userOne;
	private User userOneInvalid;


	@Mock
	private UserRepository repository;

	@Mock
	private JWTTokenGenerator tokenGenerator;

	@InjectMocks
	private UserServiceImpl service;

	
	@BeforeEach
	void setUp() {
		userOne = new User(0, EMAILONE, "one", null);
		userOneInvalid = new User(1, EMAILONE, "one@", null);
		savedUserOne = new User(1, EMAILONE, "one", null);
	}

	@Test
	public void givenUserWhenDoesNotExistsThenCreateNewUser() throws UserExistsException {

		// Configure behaviour of Mock object for this test
		when(repository.existsByEmail(anyString())).thenReturn(false);
		when(repository.save(any(User.class))).thenReturn(userOne);

		// Actual call to Service
		User user = service.registerUser(userOne);

		// Verification of Results from Service
		assertAll(() -> {
			assertNotNull(user);
			assertEquals(EMAILONE, user.getEmail());
		});

		verify(repository, times(1)).existsByEmail(anyString());
		verify(repository, atLeastOnce()).save(any(User.class));
		verifyNoMoreInteractions(repository);
	}

	@Test
	public void givenUserWhenDoesExistsThenThrowException() {
		when(repository.existsByEmail(anyString())).thenReturn(true);
		
		//Actual Call to Service
		UserExistsException userExistsException = assertThrows(UserExistsException.class, () ->{
			service.registerUser(userOne);
		});
		
		assertEquals("User with email: "+userOne.getEmail()+" already exists.", userExistsException.getMessage());
		verify(repository).existsByEmail(anyString());
		
	}
	
	@Test
	public void giveUserWhenExistsWithCorrectCredentials() {
		
		//Mock Repository and TokenGenerator
		when(repository.findByEmailOrUserId(anyString(), anyInt())).thenReturn(Optional.of(savedUserOne));
		when(tokenGenerator.generateToken(anyString())).thenReturn("Secret-Key");
		
		
		//Execute Actual Service
		Map<String,String> tokenMap= service.authenticate(userOne);
		
		//Verify the results
		assertEquals("Secret-Key", tokenMap.get("token"));
		
		verify(repository).findByEmailOrUserId(anyString(), anyInt());
		verify(tokenGenerator).generateToken(anyString());
	}
	
	@Test
	public void giveUserWhenExistsWithInCorrectCredentials() {
		when(repository.findByEmailOrUserId(anyString(), anyInt())).thenReturn(Optional.of(savedUserOne));
		
		assertThrows(ResponseStatusException.class, ()->{
			service.authenticate(userOneInvalid);
		});
		
		verify(repository).findByEmailOrUserId(anyString(), anyInt());
		verifyNoInteractions(tokenGenerator);
	}
	
}
