package org.example.libraryms.Service.Impl;

import org.example.libraryms.DTO.Auth.Request.LoginRequest;
import org.example.libraryms.DTO.Auth.Response.LoginResponse;
import org.example.libraryms.Entity.User;
import org.example.libraryms.Exception.BussinessException;
import org.example.libraryms.Mapper.UserMapper;
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
        User user = userRepository.findBySocialNumber(loginRequest.getSocialNumber());
        if(user == null) {
            throw new BussinessException("User not found");
        }
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BussinessException("Password does not match");
        }
        String token = jwtUtil.generateToken(user.getSocialNumber(), user.getRole().name());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(user.getId());
        loginResponse.setRole(user.getRole().name());
        loginResponse.setToken(token);
        return loginResponse;
    }
}
