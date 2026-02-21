package org.example.libraryms.Service.Impl;

import jakarta.transaction.Transactional;
import org.example.libraryms.DTO.Borrower.Request.BorrowerCreateRequest;
import org.example.libraryms.DTO.Borrower.Request.BorrowerSearchRequest;
import org.example.libraryms.DTO.Borrower.Request.BorrowerUpdateRequest;
import org.example.libraryms.DTO.Borrower.Response.BorrowerSearchResponse;
import org.example.libraryms.Entity.Borrower;
import org.example.libraryms.Entity.TransactionStatus;
import org.example.libraryms.Exception.BussinessException;
import org.example.libraryms.Mapper.BorrowerMapper;
import org.example.libraryms.Repository.BorrowerRepository;
import org.example.libraryms.Service.BorrowerService;
import org.example.libraryms.Specification.BorrowerSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BorrowerServiceImpl implements BorrowerService {
    private final BorrowerRepository borrowerRepository;
    private final BorrowerMapper borrowerMapper;

    public BorrowerServiceImpl(BorrowerRepository borrowerRepository, BorrowerMapper borrowerMapper) {
        this.borrowerRepository = borrowerRepository;
        this.borrowerMapper = borrowerMapper;
    }

    @Override
    public Page<BorrowerSearchResponse> search(BorrowerSearchRequest borrowerSearchRequest, Pageable pageable) {
        Specification<Borrower> spec = null;
        if (borrowerSearchRequest.getKeyword() != null) {
            spec = BorrowerSpecification.globalSearch(borrowerSearchRequest.getKeyword());
        }
        if (borrowerSearchRequest.getRegistrationDateFrom() != null || borrowerSearchRequest.getRegistrationDateTo() != null) {
            spec = spec == null ? BorrowerSpecification.registrationDateBetween(borrowerSearchRequest.getRegistrationDateFrom(), borrowerSearchRequest.getRegistrationDateTo())
                    : spec.and(BorrowerSpecification.registrationDateBetween(borrowerSearchRequest.getRegistrationDateFrom(), borrowerSearchRequest.getRegistrationDateTo()));
        }

        Page<Borrower> borrowerPage = borrowerRepository.findAll(spec, pageable);
        Page<BorrowerSearchResponse> responsePage = borrowerPage.map(borrower -> {
            BorrowerSearchResponse response = borrowerMapper.toSearchResponse(borrower);
            int borrowing = (int) borrower.getTransactions().stream()
                    .filter(t -> t.getStatus() == TransactionStatus.BORROWED)
                    .count();
            response.setBorrowingQuantity(borrowing);
            return response;
        });
        return responsePage;
    }

    @Override
    @Transactional
    public void create(BorrowerCreateRequest borrowerCreateRequest) {
        Borrower existedSocialNumber = borrowerRepository.findBySocialNumber(borrowerCreateRequest.getSocialNumber());
        if(existedSocialNumber != null) {
            throw new BussinessException("Social number already exists");
        }
        Borrower existedEmail = borrowerRepository.findByEmail(borrowerCreateRequest.getEmail());
        if(existedEmail != null) {
            throw new BussinessException("Email already exists");
        }

        Borrower newBorrower = borrowerMapper.fromCreate(borrowerCreateRequest);
        borrowerRepository.save(newBorrower);
    }

    @Override
    public void update(Integer id,BorrowerUpdateRequest borrowerUpdateRequest) {
        Borrower existedBorrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new BussinessException("Borrower not found"));

        borrowerMapper.fromUpdate(borrowerUpdateRequest, existedBorrower);
        borrowerRepository.save(existedBorrower);
    }

    @Override
    public void delete(Integer id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new BussinessException("Borrower not found"));
        if (!borrower.getTransactions().isEmpty()) {
            throw new BussinessException("User is borrowing books, cannot delete");
        }

        borrowerRepository.delete(borrower);
    }
}
