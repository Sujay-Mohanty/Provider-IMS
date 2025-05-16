package com.provider.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class TokenService {
    public String generateToken(String email, String role) {
        // Replace this with actual JWT generation
        return UUID.randomUUID().toString() + "-" + email;
    }
}