package org.example.libraryms.Service.Impl;

import org.example.libraryms.DTO.User.Request.UserSearchRequest;
import org.example.libraryms.DTO.User.Response.UserSearchResponse;
import org.example.libraryms.Entity.User;
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

    public UserServiceImpl (UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Page<UserSearchResponse> search(UserSearchRequest userSearchRequest, Pageable pageable) {
        Specification<User> spec = null;
        if(userSearchRequest.getKeyword() != null){
            spec = UserSpecification.globalSearch(userSearchRequest.getKeyword());
        }
        if(userSearchRequest.getRole() != null){
            spec = spec == null ? UserSpecification.globalSearch(userSearchRequest.getRole())
                : spec.and(UserSpecification.globalSearch(userSearchRequest.getRole()));
        }

        Page<User> userPage = userRepository.findAll(spec, pageable);
        Page<UserSearchResponse> responsePage = userPage.map(userMapper::toSearchResponse);
        return responsePage;
    }
}
