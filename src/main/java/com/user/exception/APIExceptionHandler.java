package com.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class APIExceptionHandler {
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<?> handleUserNotFound(UserNotFoundException exception){
		APIError error = new APIError("Not Found",exception.getMessage());
		return new ResponseEntity<APIError>(error,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(UserExistsException.class)
	public ResponseEntity<?> handleUserExists(UserExistsException exception){
		APIError error = new APIError("User Exists",exception.getMessage());
		return new ResponseEntity<APIError>(error,HttpStatus.CONFLICT);
	}
	
	public ResponseEntity<?> handleInvalidArguments(MethodArgumentNotValidException exception){
		List<String> errors = exception.getBindingResult().getAllErrors()
				.stream().map(error -> ((FieldError)error).getField() + " : " + error.getDefaultMessage())
				.collect(Collectors.toList());
		
		APIError error = new APIError("Invalid Request", errors.stream().collect(Collectors.joining(",","[","]")));
		
		return new ResponseEntity<APIError>(error,HttpStatus.BAD_REQUEST);
				
	}
}
