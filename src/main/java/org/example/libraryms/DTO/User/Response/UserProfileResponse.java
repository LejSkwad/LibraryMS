package org.example.libraryms.DTO.User.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Integer id;
    private String memberId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String role;
}
