package com.pact.pact_backend.controller;

import com.pact.pact_backend.dto.request.LoginRequest;
import com.pact.pact_backend.dto.request.RegisterRequest;
import com.pact.pact_backend.dto.response.AuthResponse;
import com.pact.pact_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.loginUser(request);
    }
}
