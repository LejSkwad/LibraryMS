package org.example.libraryms.DTO.User.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchResponse {
    private Integer id;
    private String memberId;
    private String email;
    private String fullName;
    private String role;
    private String phone;
    private String address;
    private String registrationDate;
    private Integer borrowingCount;
}
