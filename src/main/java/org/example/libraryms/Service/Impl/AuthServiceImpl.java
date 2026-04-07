package org.example.libraryms.Service.Impl;

import org.example.libraryms.DTO.Auth.Request.LoginRequest;
import org.example.libraryms.DTO.Auth.Response.LoginResponse;
import org.example.libraryms.Entity.User;
import org.example.libraryms.Exception.BussinessException;
import org.example.libraryms.Repository.UserRepository;
import org.example.libraryms.Security.JwtUtil;
import org.example.libraryms.Service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null || user.getPassword() == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BussinessException("Sai thông tin đăng nhập");
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(user.getId());
        loginResponse.setRole(user.getRole().name());
        loginResponse.setToken(token);
        return loginResponse;
    }
}
