package org.example.demostudentmanagementsystem.service;

import org.example.demostudentmanagementsystem.dto.RegisterRequest;
import org.example.demostudentmanagementsystem.dto.RegisterResponse;

/**
 * Service interface defining authentication and user registration operations.
 */
public interface AuthService {

    /**
     * Registers a new user account in the system.
     *
     * @param request The registration payload containing user details
     * @return RegisterResponse containing created user details without sensitive data
     */
    RegisterResponse register(RegisterRequest request);
}
