package org.example.libraryms.DTO.Auth.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String role;
    private String socialNumber;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
}
