package org.example.libraryms.Controller;

import jakarta.validation.Valid;
import org.example.libraryms.Common.BaseResponse;
import org.example.libraryms.DTO.Auth.Request.ClaimAccountRequest;
import org.example.libraryms.DTO.Auth.Request.LoginRequest;
import org.example.libraryms.DTO.Auth.Request.RegisterRequest;
import org.example.libraryms.DTO.Auth.Response.LoginResponse;
import org.example.libraryms.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/v1/auth/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest){
        LoginResponse data = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(data, "Đăng nhập thành công"));
    }

    @PostMapping("/v1/auth/register")
    public ResponseEntity<BaseResponse<Void>> register(@Valid @RequestBody RegisterRequest registerRequest){
        authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null, "Đăng ký thành công"));
    }

    @PostMapping("/v1/auth/claim")
    public ResponseEntity<BaseResponse<Void>> claimAccount(@Valid @RequestBody ClaimAccountRequest request){
        authService.claimAccount(request);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null, "Liên kết tài khoản thành công"));
    }
}
