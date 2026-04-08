package org.example.libraryms.Service;

import org.example.libraryms.DTO.Auth.Request.ClaimAccountRequest;
import org.example.libraryms.DTO.Auth.Request.LoginRequest;
import org.example.libraryms.DTO.Auth.Request.RegisterRequest;
import org.example.libraryms.DTO.Auth.Response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
    void claimAccount(ClaimAccountRequest request);
}
