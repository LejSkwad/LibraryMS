package org.example.libraryms.Repository;

import org.example.libraryms.Entity.BorrowRequest;
import org.example.libraryms.Entity.BorrowRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Integer>, JpaSpecificationExecutor<BorrowRequest> {

    @EntityGraph(attributePaths = {"user"})
    Page<BorrowRequest> findAll(Specification<BorrowRequest> spec, Pageable pageable);

    @Query("SELECT br FROM BorrowRequest br JOIN FETCH br.borrowRequestItem bri JOIN FETCH bri.book WHERE br.id = :id")
    Optional<BorrowRequest> findWithItemsAndBooks(@Param("id") Integer id);

    boolean existsByUser_IdAndStatusIn(Integer userId, List<BorrowRequestStatus> statuses);
}
