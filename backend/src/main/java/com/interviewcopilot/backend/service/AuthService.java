package com.interviewcopilot.backend.service;

import com.interviewcopilot.backend.dto.request.LoginRequest;
import com.interviewcopilot.backend.dto.request.RegisterRequest;
import com.interviewcopilot.backend.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}