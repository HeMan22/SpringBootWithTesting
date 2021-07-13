package com.user.service;

import java.util.Map;

import com.user.exception.UserExistsException;
import com.user.model.User;

public interface UserService {
	public User registerUser(User user) throws UserExistsException;

	public Map<String, String> authenticate(User user);

}
