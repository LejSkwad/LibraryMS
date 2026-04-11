package org.example.libraryms.Service.Impl;

import jakarta.transaction.Transactional;
import org.example.libraryms.DTO.BorrowRequest.Request.BRApproveRequest;
import org.example.libraryms.DTO.BorrowRequest.Request.BRCreateRequest;
import org.example.libraryms.DTO.BorrowRequest.Request.BRSearchRequest;
import org.example.libraryms.DTO.BorrowRequest.Response.BRSearchResponse;
import org.example.libraryms.Entity.*;
import org.example.libraryms.Exception.BussinessException;
import org.example.libraryms.Repository.*;
import org.example.libraryms.Service.BorrowRequestService;
import org.example.libraryms.Service.SseService;
import org.example.libraryms.Specification.BorrowRequestSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BorrowRequestServiceImpl implements BorrowRequestService {
    private final BorrowRequestRepository borrowRequestRepository;

    public BorrowRequestServiceImpl(BorrowRequestRepository borrowRequestRepository) {
        this.borrowRequestRepository = borrowRequestRepository;
    }

    @Override
    public Page<BRSearchResponse> search(BRSearchRequest brSearchRequest, Pageable pageable) {



        return null;
    }
}
