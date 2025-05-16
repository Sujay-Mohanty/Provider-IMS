package com.provider.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.provider.entity.User;
import com.provider.repository.UserRepository;



@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	public void addUser(User user) {
		userRepository.save(user);
	}
	
	public Optional<User> findByEmailAndPassword(User user) {
		return userRepository.findByEmailAndPassword(user.getEmail(),user.getPassword());
	}

	public Optional<User> findByName(String name) {
		// TODO Auto-generated method stub
		return userRepository.findByName(name);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email).get();
	}
	
	
}