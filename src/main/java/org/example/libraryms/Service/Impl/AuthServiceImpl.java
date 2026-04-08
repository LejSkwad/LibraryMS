package org.example.libraryms.Service.Impl;

import jakarta.transaction.Transactional;
import org.example.libraryms.DTO.Auth.Request.ClaimAccountRequest;
import org.example.libraryms.DTO.Auth.Request.LoginRequest;
import org.example.libraryms.DTO.Auth.Request.RegisterRequest;
import org.example.libraryms.DTO.Auth.Response.LoginResponse;
import org.example.libraryms.Entity.Role;
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
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
            throw new BussinessException("Email đã tồn tại");
        }
        User newUser = userMapper.fromRegister(registerRequest);
        newUser.setRole(Role.BORROWER);
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.saveAndFlush(newUser);
        newUser.setMemberId("LIB-" + String.format("%06d", newUser.getId()));
        userRepository.save(newUser);
    }

    @Override
    @Transactional
    public void claimAccount(ClaimAccountRequest request) {
        User existing = userRepository.findByMemberId(request.getMemberId());
        if (existing == null) {
            throw new BussinessException("Không tìm thấy mã thẻ thành viên");
        }
        if (existing.getEmail() != null && !existing.getEmail().isBlank()) {
            throw new BussinessException("Thẻ thành viên này đã được liên kết với tài khoản khác");
        }
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new BussinessException("Email đã tồn tại");
        }
        if (!existing.getFirstName().trim().equalsIgnoreCase(request.getFirstName().trim()) ||
            !existing.getLastName().trim().equalsIgnoreCase(request.getLastName().trim())) {
            throw new BussinessException("Thông tin họ tên không khớp với hồ sơ thư viện");
        }
        existing.setEmail(request.getEmail());
        existing.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(existing);
    }
}
