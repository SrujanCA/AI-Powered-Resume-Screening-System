package com.resume.screening.service;

import com.resume.screening.dto.AuthResponse;
import com.resume.screening.dto.LoginRequest;
import com.resume.screening.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest);

    AuthResponse getCurrentUser(String email);
}
