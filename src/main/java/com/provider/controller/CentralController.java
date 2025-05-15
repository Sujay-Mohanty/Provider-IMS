package com.provider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.provider.entity.User;
import com.provider.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class CentralController {

	@Autowired
    private UserService userService;
	
	@GetMapping("/")
	public String showRegisterUserForm(Model model) {
		model.addAttribute("user",new User());
		return "register";
	}
	
	@PostMapping("/register")
	public String registerUser(@ModelAttribute User user) {
		userService.addUser(user);
		return "redirect:/login";
	}
	
	@GetMapping("/login")
	public String loginForm(Model model) {
		model.addAttribute("user",new User());
		return "login";
	}
	
@PostMapping("/loginVal")
public String loginValidation(@ModelAttribute User user, Model model, HttpSession session) {
    User usernow = userService.findByEmailAndPassword(user);
    
    if (usernow != null) {
        session.setAttribute("loggedInUser", usernow);  // ✅ Store user in session
        return "redirect:/user/userHome";
    } else if ("admin@gmail.com".equals(user.getEmail()) && "admin".equals(user.getPassword())) {
        session.setAttribute("admin", "admin"); // optional admin flag
        return "redirect:/admin/adminHome";
    } else {
        return "redirect:/login?error=true";
    }
}
	

	
	
}
