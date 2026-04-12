package org.example.libraryms.Specification;

import org.example.libraryms.Entity.BorrowRequest;
import org.example.libraryms.Entity.BorrowRequestStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class BorrowRequestSpecification {
    public static Specification<BorrowRequest> nameLike(String name) {
        return (root, query, builder) -> builder.or(
                builder.like(builder.lower(root.get("user").get("firstName")), "%" + name.toLowerCase() + "%"),
                builder.like(builder.lower(root.get("user").get("lastName")),  "%" + name.toLowerCase() + "%")
        );
    }

    public static Specification<BorrowRequest> memberIdEqual(String memberId) {
        return (root, query, builder)
                -> builder.equal(root.get("user").get("memberId"), memberId);
    }

    public static Specification<BorrowRequest> requestDateBetween(LocalDate from, LocalDate to) {
        return (root, query, builder) -> {
            LocalDateTime start = from != null ? from.atStartOfDay() : null;
            LocalDateTime end   = to   != null ? to.atTime(23, 59, 59) : null;
            if (start != null && end != null) return builder.between(root.get("requestDate"), start, end);
            if (start != null) return builder.greaterThanOrEqualTo(root.get("requestDate"), start);
            return builder.lessThanOrEqualTo(root.get("requestDate"), end);
        };
    }

    public static Specification<BorrowRequest> statusEqual(String status) {
        return (root, query, builder)
                -> builder.equal(root.get("status"), BorrowRequestStatus.valueOf(status));
    }
}
