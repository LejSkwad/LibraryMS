package org.example.libraryms.Service.Impl;

import jakarta.transaction.Transactional;
import org.example.libraryms.DTO.User.Request.UserCreateRequest;
import org.example.libraryms.DTO.User.Request.UserSearchRequest;
import org.example.libraryms.DTO.User.Request.UserUpdateRequest;
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
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    //private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper
                           ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        //this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<UserSearchResponse> search(UserSearchRequest userSearchRequest, Pageable pageable) {
        Specification<User> spec = null;
        if (userSearchRequest.getKeyword() != null) {
            spec = UserSpecification.globalSearch(userSearchRequest.getKeyword());
        }
        if(userSearchRequest.getRole() != null) {
            spec = spec == null ? UserSpecification.roleEqual(userSearchRequest.getRole())
                    : spec.and(UserSpecification.roleEqual(userSearchRequest.getRole()));
        }
        if(userSearchRequest.getCreateDateFrom() != null || userSearchRequest.getCreateDateTo() != null) {
            spec = spec == null ? UserSpecification.registrationDateBetween(userSearchRequest.getCreateDateFrom(), userSearchRequest.getCreateDateTo())
                    : spec.and(UserSpecification.registrationDateBetween(userSearchRequest.getCreateDateFrom(), userSearchRequest.getCreateDateTo()));
        }

        Page<User> userPage = userRepository.findAll(spec, pageable);
        Page<UserSearchResponse> responsePage = userPage.map(user -> {
            UserSearchResponse response = userMapper.toSearchResponse(user);
            response.setBorrowingCount((int) user.getTransactions()
                    .stream()
                    .filter(t -> t.getStatus() == TransactionStatus.BORROWED)
                    .count());
            return response;
        });

        return responsePage;
    }

    @Override
    @Transactional
    public void create(UserCreateRequest userCreateRequest) {
        User user = userRepository.findBySocialNumber(userCreateRequest.getSocialNumber());
        if(user != null) {
            throw new BussinessException("Social Number has already been used");
        }

        User newUser = userMapper.fromCreate(userCreateRequest);
        newUser.setPassword(userCreateRequest.getPassword());

        userRepository.save(newUser);
    }

    @Override
    @Transactional
    public void update(Integer id, UserUpdateRequest userUpdateRequest) {
        User existedUser = userRepository.findById(id)
                .orElseThrow(() -> new BussinessException("User not found"));

        userMapper.fromUpdate(userUpdateRequest, existedUser);
        userRepository.save(existedUser);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BussinessException("User not found"));
        if (user.getRole() == Role.BORROWER && !user.getTransactions().isEmpty()) {
            throw new BussinessException("Cannot delete borrower account with transactions history");
        }
        userRepository.delete(user);
    }
}
