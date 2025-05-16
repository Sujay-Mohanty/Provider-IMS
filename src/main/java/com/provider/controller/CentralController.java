package com.provider.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.provider.dto.LoginResponseDTO;
import com.provider.entity.User;
import com.provider.service.TokenService;
import com.provider.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/")
public class CentralController {

	 @Autowired
	    private UserService userService;
	 
	 @Autowired
	 private TokenService tokenService;

	    /**
	     * Register a new user
	     */
	    @PostMapping("/register")
	    public ResponseEntity<String> registerUser(@RequestBody User user) {
	        try {
	            userService.addUser(user);
	            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
	        }
	    }

	    /**
	     * Validate user credentials (login)
	     */
//	    @PostMapping("/login")
//	    public ResponseEntity<String> loginValidation(@RequestBody User user, HttpSession session) {
//	        if ("admin@gmail.com".equals(user.getEmail()) && "admin".equals(user.getPassword())) {
//	            session.setAttribute("admin", "admin");
//	            return ResponseEntity.ok("ADMIN");
//	        }
//
//	        User existingUser = userService.findByEmailAndPassword(user);
//	        if (existingUser != null) {
//	            session.setAttribute("loggedInUser", existingUser);
//	            return ResponseEntity.ok("USER");
//	        } else {
//	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//	        }
//	    }
	    @PostMapping("/login")
	    public ResponseEntity<LoginResponseDTO> login(@RequestBody User user) {
	        if ("admin@gmail.com".equals(user.getEmail()) && "admin".equals(user.getPassword())) {
	            return ResponseEntity.ok(new LoginResponseDTO("Admin", "admin@gmail.com", 0L, "ADMIN"));
	        }

	        Optional<User> optionalUser = userService.findByEmailAndPassword(user);
	        if (optionalUser.isPresent()) {
	            User existingUser = optionalUser.get();
	            return ResponseEntity.ok(new LoginResponseDTO(
	                existingUser.getName(),
	                existingUser.getEmail(),
	                existingUser.getId(),
	                "USER"
	            ));
	        }

	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	    }

	    /**
	     * Logout endpoint: invalidates session
	     */
	    @PostMapping("/logout")
	    public ResponseEntity<String> logout(HttpSession session) {
	        session.invalidate();
	        return ResponseEntity.ok("Logged out successfully");
	    }
	
}
