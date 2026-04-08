package org.example.libraryms.Service.Impl;

import jakarta.transaction.Transactional;
import org.example.libraryms.DTO.User.Request.ChangePasswordRequest;
import org.example.libraryms.DTO.User.Request.UserCreateRequest;
import org.example.libraryms.DTO.User.Request.UserSearchRequest;
import org.example.libraryms.DTO.User.Request.UserUpdateRequest;
import org.example.libraryms.DTO.User.Response.UserProfileResponse;
import org.example.libraryms.DTO.User.Response.UserSearchResponse;
import org.example.libraryms.Entity.Role;
import org.example.libraryms.Entity.TransactionStatus;
import org.example.libraryms.Entity.User;
import org.example.libraryms.Exception.BussinessException;
import org.example.libraryms.Mapper.UserMapper;
import org.example.libraryms.Repository.UserRepository;
import org.example.libraryms.Service.UserService;
import org.example.libraryms.Specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<UserSearchResponse> search(UserSearchRequest userSearchRequest, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isLibrarian = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"));
        if (isLibrarian) {
            userSearchRequest.setRole(String.valueOf(Role.BORROWER));
        }
        Specification<User> spec = (root, query, builder) -> builder.conjunction();
        if (userSearchRequest.getKeyword() != null) {
            spec = spec.and(UserSpecification.globalSearch(userSearchRequest.getKeyword()));
        }
        if (userSearchRequest.getRole() != null) {
            spec = spec.and(UserSpecification.roleEqual(userSearchRequest.getRole()));
        }
        if (userSearchRequest.getCreateDateFrom() != null || userSearchRequest.getCreateDateTo() != null) {
            spec = spec.and(UserSpecification.registrationDateBetween(
                    userSearchRequest.getCreateDateFrom(), userSearchRequest.getCreateDateTo()));
        }

        return userRepository.findAll(spec, pageable).map(user -> {
            UserSearchResponse response = userMapper.toSearchResponse(user);
            response.setBorrowingCount((int) user.getTransactions().stream()
                    .filter(t -> t.getStatus() == TransactionStatus.BORROWED)
                    .count());
            return response;
        });
    }

    @Override
    public UserProfileResponse getProfile(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BussinessException("User not found"));
        return userMapper.toProfileResponse(user);
    }

    @Override
    @Transactional
    public void create(UserCreateRequest userCreateRequest) {
        if (userCreateRequest.getEmail() != null && !userCreateRequest.getEmail().isBlank()) {
            if (userRepository.findByEmail(userCreateRequest.getEmail()) != null) {
                throw new BussinessException("Email đã tồn tại");
            }
            if (userCreateRequest.getPassword() == null || userCreateRequest.getPassword().isBlank()) {
                throw new BussinessException("Yêu cầu nhập mật khẩu");
            }
        }

        User newUser = userMapper.fromCreate(userCreateRequest);
        if (userCreateRequest.getPassword() != null && !userCreateRequest.getPassword().isBlank()) {
            newUser.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
        }
        userRepository.saveAndFlush(newUser);
        newUser.setMemberId("LIB-" + String.format("%06d", newUser.getId()));
        userRepository.save(newUser);
    }

    @Override
    @Transactional
    public void update(Integer id, UserUpdateRequest userUpdateRequest) {
        User existedUser = userRepository.findById(id)
                .orElseThrow(() -> new BussinessException("Không tìm thấy tài khoản"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isLibrarian = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"));
        String authEmail = auth.getName();

        if (isLibrarian && existedUser.getRole() != Role.BORROWER
                && !existedUser.getEmail().equals(authEmail)) {
            throw new BussinessException("Không có quyền cập nhật tài khoản này");
        }
        userMapper.fromUpdate(userUpdateRequest, existedUser);
        userRepository.save(existedUser);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BussinessException("Không tìm thấy tài khoản"));
        if (user.getRole() == Role.BORROWER && !user.getTransactions().isEmpty()) {
            throw new BussinessException("Không thể xóa tài khoản đã có lịch sử mượn");
        }
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void changePassword(Integer id, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BussinessException("Không tìm thấy tài khoản"));

        String authEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!authEmail.equals(user.getEmail())) {
            throw new BussinessException("Không thể thay đổi mật khẩu");
        }
        if (user.getPassword() == null
                || !passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BussinessException("Sai mật khẩu");
        }
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getNewPasswordConfirmation())) {
            throw new BussinessException("Mật khẩu mới không khớp");
        }
        if(passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new BussinessException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }
}
