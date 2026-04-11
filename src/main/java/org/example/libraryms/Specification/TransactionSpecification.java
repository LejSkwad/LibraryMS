package org.example.libraryms.Specification;

import org.example.libraryms.Entity.Transaction;
import org.example.libraryms.Entity.TransactionStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TransactionSpecification {
    public static Specification<Transaction> nameLike(String keyword){
        return ((root, query, builder) -> {
            return builder.or(
                    builder.like(builder.lower(root.get("user").get("firstName")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("user").get("lastName")), "%" + keyword.toLowerCase() + "%")
            );
        });
    }

    public static Specification<Transaction> memberIdLike(String memberId){
        return ((root, query, builder) -> {
            return builder.like(builder.lower(root.get("user").get("memberId")), "%" + memberId.toLowerCase() + "%");
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

    public static Specification<Transaction> borrowDateBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            if (from != null && to != null) return builder.between(root.get("borrowDate"), from, to);
            if (from != null) return builder.greaterThanOrEqualTo(root.get("borrowDate"), from);
            return builder.lessThanOrEqualTo(root.get("borrowDate"), to);
        };
    }

    public static Specification<Transaction> dueDateBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            if (from != null && to != null) return builder.between(root.get("dueDate"), from, to);
            if (from != null) return builder.greaterThanOrEqualTo(root.get("dueDate"), from);
            return builder.lessThanOrEqualTo(root.get("dueDate"), to);
        };
    }

    public static Specification<Transaction> returnDateBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            if (from != null && to != null) return builder.between(root.get("returnDate"), from, to);
            if (from != null) return builder.greaterThanOrEqualTo(root.get("returnDate"), from);
            return builder.lessThanOrEqualTo(root.get("returnDate"), to);
        };
    }
}
