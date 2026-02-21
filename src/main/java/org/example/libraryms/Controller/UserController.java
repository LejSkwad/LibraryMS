package org.example.libraryms.Controller;

import org.example.libraryms.Common.BaseResponse;
import org.example.libraryms.DTO.User.Request.UserSearchRequest;
import org.example.libraryms.DTO.User.Response.UserSearchResponse;
import org.example.libraryms.Service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/v1/users")
    public ResponseEntity<BaseResponse<Page<UserSearchResponse>>> getUsers(UserSearchRequest userSearchRequest, Pageable pageable) {
        Page<UserSearchResponse> data = userService.search(userSearchRequest, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(data, "Lay users thanh cong"));
    }

    // TODO: finish UserController + TransactionController
}
