package org.example.libraryms.DTO.User.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest {
    @NotBlank
    private String memberId;

    // optional — walk-in accounts may have no email
    private String email;

    // optional — only required if email is provided (enforced in service)
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String phone;

    @NotBlank
    private String role;

    private String address;
}
