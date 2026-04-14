package org.example.libraryms.Repository;

import org.example.libraryms.Entity.Transaction;
import org.example.libraryms.Entity.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer>, JpaSpecificationExecutor<Transaction> {

    @EntityGraph(attributePaths = {"user"})
    Page<Transaction> findAll(Specification<Transaction> spec, Pageable pageable);

    @Query("SELECT t FROM Transaction t JOIN FETCH t.items i JOIN FETCH i.book WHERE t.id = :id")
    Optional<Transaction> findWithItemsAndBooks(@Param("id") Integer id);

    long countByUser_IdAndStatus(Integer userId, TransactionStatus status);

    boolean existsByUser_Id(Integer userId);

    @Query("SELECT t.user.id, COUNT(t) FROM Transaction t WHERE t.user.id IN :userIds AND t.status = :status GROUP BY t.user.id")
    List<Object[]> countByUserIdsAndStatus(@Param("userIds") List<Integer> userIds, @Param("status") TransactionStatus status);
}
