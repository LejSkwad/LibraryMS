package org.example.libraryms.Repository;

import org.example.libraryms.Entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, Integer>, JpaSpecificationExecutor<Borrower> {
    Borrower findBySocialNumber(String socialNumber);
    Borrower findByEmail(String email);

    Borrower findByEmailLike(String email);
}
