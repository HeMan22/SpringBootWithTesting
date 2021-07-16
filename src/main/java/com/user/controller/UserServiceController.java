package com.user.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.exception.UserExistsException;
import com.user.model.User;
import com.user.service.UserService;

@RestController
@RequestMapping("/api/vi/users")
public class UserServiceController {
	
	@Autowired
    private UserService service;

	@GetMapping
	public String appInfo() {
		return "User Service is up and running";
	}
	@PostMapping("/register")
    public String registerUser(@RequestBody User user) throws UserExistsException{

		System.out.println("Entering register function");
        User registeredUser = service.registerUser(user);
        return String.join(":", "User Registered ", registeredUser.getUserId().toString());

    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {
        return service.authenticate(user);
    }

}
