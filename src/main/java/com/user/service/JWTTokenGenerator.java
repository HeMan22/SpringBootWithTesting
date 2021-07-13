package com.user.service;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@Component
public class JWTTokenGenerator {
	
	private static final long EXPIRATION_DURATION = 30 * 60 * 1000l;
	private static final String SECRET_KEY = "somesecretkey";

	public String generateToken(String email) {

		return Jwts.builder().setIssuer("myorg").setSubject(email)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_DURATION))
				.signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();

	}


}
