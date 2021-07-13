package com.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

//  https://docs.spring.io/spring-data/jpa/docs/2.5.2/reference/html/#repository-query-keywords

	boolean existsByEmail(String email);

	Optional<User> findByEmailOrUserId(String email, Integer userId);

	Optional<User> findByEmailAndPasswordOrUserIdAndPassword(String email, String password, Integer userId,
			String password2);

	/* Creating custom queries with @Query */
	@Query("Select count(u) from User u where email = ?1")
	Long countByEmail(String email);

}
