package com.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.print.Printable;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.exception.UserExistsException;
import com.user.model.User;
import com.user.service.UserService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserServiceController.class)
public class UserServiceControllerTest {

	public static final String REGISTER_ENDPOINT = "/api/vi/users/register";
	public static final String LOGIN_ENDPOINT = "/api/vi/users/login";
	private static final String EMAILONE = "one@mail.com";
	private User savedUserOne;
	private User userOne;
	private User userOneInvalid;


	@Autowired
	private ObjectMapper mapper; // Converts String to Json and Vice versa. Present in Jackson

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService service;
	

	@BeforeEach
	public void setUp() {

		userOne = new User(0, EMAILONE, "one", null);
		savedUserOne = new User(1, EMAILONE, "one", null);
		userOneInvalid = new User(0, EMAILONE, "one@", null);
	}

	@Test
	public void givenUserDetailsWhenUserDoesNotExistThenRegistrationSuccessful() throws Exception {

		// Setup the mock bean behaviour
		when(service.registerUser(any(User.class))).thenReturn(savedUserOne);

		System.out.println(mapper.writeValueAsString(userOne));
		MvcResult mvcResult = mvc
						.perform(post(REGISTER_ENDPOINT)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(userOne))) /* Conversion of the userOne Object into JSON */
						.andExpect(status().isOk())
						.andDo(MockMvcResultHandlers.print())
						.andReturn();

		//assertTrue(mvcResult.getResponse().getContentAsString().equals("User Registered :1"));
		assertEquals("User Registered :1", mvcResult.getResponse().getContentAsString());
		verify(service).registerUser(any(User.class));
	}
	
    @Test
    public void givenUserDetailsWhenUserExistsThenReturnConflictStatus() throws Exception  {
        // Setup the mock bean behaviour
        when(service.registerUser(any(User.class))).thenThrow(UserExistsException.class);
        // send the request to the endpoint
        // verify the status code returned
        mvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userOne)))
                .andExpect(status().isConflict());

        verify(service).registerUser(any(User.class));
    }

	@Test
	public void givenUserDetailsWhenUserCredentialsValidThenReturnToken() throws JsonProcessingException, Exception {
		
		when(service.authenticate(any(User.class))).thenReturn(Map.of("token","secret-token"));
		
		MvcResult mvcResult = mvc.perform(post(LOGIN_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(userOne)))
		.andExpect(status().isOk())
		.andDo(MockMvcResultHandlers.print())
		.andReturn();
		
		Map expectedResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);
		
		assertEquals("secret-token", expectedResponse.get("token"));
	}

}
