package org.example.libraryms.Controller;

import jakarta.validation.Valid;
import org.example.libraryms.Common.BaseResponse;
import org.example.libraryms.DTO.User.Request.ChangePasswordRequest;
import org.example.libraryms.DTO.User.Request.UserCreateRequest;
import org.example.libraryms.DTO.User.Request.UserSearchRequest;
import org.example.libraryms.DTO.User.Request.UserUpdateRequest;
import org.example.libraryms.DTO.User.Response.UserProfileResponse;
import org.example.libraryms.DTO.User.Response.UserSearchResponse;
import org.example.libraryms.Service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/v1/users")
    public ResponseEntity<BaseResponse<Page<UserSearchResponse>>> search(@ModelAttribute UserSearchRequest userSearchRequest, Pageable pageable){
        Page<UserSearchResponse> data = userService.search(userSearchRequest, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(data, "Lay user thanh cong"));
    }

    @GetMapping("/v1/users/profile/{id}")
    public ResponseEntity<BaseResponse<UserProfileResponse>> getProfile(@PathVariable Integer id){
        UserProfileResponse data = userService.getProfile(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(data, "Lay thong tin user thanh cong"));
    }

    @PostMapping("/v1/users")
    public ResponseEntity<BaseResponse<Void>> create(@Valid @RequestBody UserCreateRequest userCreateRequest){
        userService.create(userCreateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null, "Tao User thanh cong"));
    }

    @PutMapping("/v1/users/{id}")
    public ResponseEntity<BaseResponse<Void>> update(@PathVariable Integer id, @Valid @RequestBody UserUpdateRequest userUpdateRequest){
        userService.update(id, userUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null, "Cap nhat User thanh cong"));
    }

    @DeleteMapping("/v1/users/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Integer id){
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null, "Xoa User thanh cong"));
    }

    @PutMapping("/v1/users/change-password/{id}")
    public ResponseEntity<BaseResponse<Void>> changePassword(@PathVariable Integer id, @Valid @RequestBody ChangePasswordRequest changePasswordRequest){
        userService.changePassword(id, changePasswordRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(null, "thay doi mat khau thanh cong"));
    }
}
