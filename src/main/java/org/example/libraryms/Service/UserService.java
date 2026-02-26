package org.example.libraryms.Service;

import org.example.libraryms.DTO.User.Request.UserCreateRequest;
import org.example.libraryms.DTO.User.Request.UserSearchRequest;
import org.example.libraryms.DTO.User.Response.UserSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserSearchResponse> search(UserSearchRequest userSearchRequest, Pageable pageable);
    void create(UserCreateRequest userCreateRequest);
}
