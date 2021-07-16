package com.user.service;

import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.user.exception.UserExistsException;
import com.user.model.User;
import com.user.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService{

	private UserRepository repository;
    private JWTTokenGenerator tokenGenerator;

    @Autowired
    public UserServiceImpl(UserRepository repository, JWTTokenGenerator tokenGenerator) {
    	super();
    	this.repository=repository;
    	this.tokenGenerator=tokenGenerator;
    }
    
    @Override
    public User registerUser(User user) throws UserExistsException {

    	System.out.println("user Service");
    	
        boolean userExists = repository.existsByEmail(user.getEmail());
        if(userExists) {
            throw new UserExistsException("User with email: "+user.getEmail()+" already exists.");
        }

//        UserRole userRole = roleRepository.getRole(1);
//        user.setUserRole(userRole);
        return repository.save(user);
    }

    @Override
    public Map<String, String> authenticate(User user) {
        User dbUser = null;

//        Optional<User> userIdAndPassword = repository.findByEmailAndPasswordOrUserIdAndPassword(user.getEmail(), user.getPassword(), user.getUserId(), user.getPassword());
//        System.out.println(userIdAndPassword);
        System.out.println("COUNT: "+repository.countByEmail(user.getEmail()));


        Optional<User> optionalUser = repository.findByEmailOrUserId(user.getEmail(), user.getUserId());

        if(optionalUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        dbUser = optionalUser.get();
        //check the creds and generate the token
        if(user.getPassword().equals(dbUser.getPassword())) {
            return Map.of("token",tokenGenerator.generateToken(dbUser.getEmail()));
        }else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credential Mismatch");
        }
    }

	

}
