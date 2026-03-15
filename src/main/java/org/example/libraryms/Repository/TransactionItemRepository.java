package org.example.libraryms.Repository;

import org.example.libraryms.Entity.TransactionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionItemRepository extends JpaRepository<TransactionItem, Integer> {
    List<TransactionItem> findByTransactionId(Integer transactionId);
}
