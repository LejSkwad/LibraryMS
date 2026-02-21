package org.example.libraryms.DTO.User.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchResponse {
    private Integer id;
    private String username;
    private String fullName;
    private String role;
}
