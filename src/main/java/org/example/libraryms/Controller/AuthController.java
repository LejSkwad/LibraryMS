package org.example.libraryms.Controller;

import org.example.libraryms.Common.BaseResponse;
import org.example.libraryms.DTO.Auth.Request.LoginRequest;
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
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(data, "dang nhap thanh cong"));
    }
}
