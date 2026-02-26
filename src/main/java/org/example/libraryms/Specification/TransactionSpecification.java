package org.example.libraryms.Specification;

import org.example.libraryms.Entity.Transaction;
import org.example.libraryms.Entity.TransactionStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TransactionSpecification {
    public static Specification<Transaction> globalSearch(String keyword){
        return ((root, query, builder) -> {
            return builder.or(
                    builder.like(builder.lower(root.get("book").get("title")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("user").get("firstName")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("user").get("lastName")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("user").get("socialNumber")), "%" + keyword.toLowerCase() + "%")
            );
        });
    }

    public static Specification<Transaction> statusEqual (String status){
        return ((root, query, builder) -> {
            if (status.equals("BORROWED")){
                return builder.and(
                        builder.equal(root.get("status"), TransactionStatus.BORROWED),
                        builder.greaterThanOrEqualTo(root.get("dueDate"), LocalDate.now())
                );
            }
            if(status.equals("OVERDUE")){
                return builder.and(
                        builder.equal(root.get("status"), TransactionStatus.BORROWED),
                        builder.lessThan(root.get("dueDate"), LocalDate.now())
                );
            }
            if(status.equals("RETURNED")){
                return builder.equal(root.get("status"), TransactionStatus.RETURNED);
            }
            return builder.conjunction();
        });

    }
}
