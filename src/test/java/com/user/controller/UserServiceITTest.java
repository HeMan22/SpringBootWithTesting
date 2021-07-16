package com.user.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.model.User;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class UserServiceITTest {


    public static final String REGISTER_ENDPOINT = "/api/v1/users/register";
    public static final String LOGIN_ENDPOINT = "/api/v1/users/login";
    private User userOne;
    private User savedUserOne;
    private User userOneInvalid;
    public static final String EMAILONE = "one@mail.com";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;


    @BeforeEach
    void setUp() {
        userOne = new User(0, EMAILONE, "one", null);
        userOneInvalid = new User(0, EMAILONE, "one@", null);
        savedUserOne = new User(1, EMAILONE, "one", null);
    }


    @Test
    public void givenUserDetailsWhenUserDoesNotExistThenRegistrationSuccessful() throws Exception {

        MvcResult mvcResult = mvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userOne)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        assertEquals("User Registered : 1", mvcResult.getResponse().getContentAsString());
    }


}
