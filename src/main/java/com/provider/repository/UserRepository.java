package com.provider.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.provider.entity.Product;
import com.provider.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

	public Optional<User> findByEmailAndPassword(String email,String password);
	//Should return an optional 
	
	public Optional<User> findByName(String name);

	public Optional<User> findByEmail(String email);
}
