package org.example.libraryms.Repository;

import org.example.libraryms.Entity.BorrowRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Integer>, JpaSpecificationExecutor<BorrowRequest> {
}
