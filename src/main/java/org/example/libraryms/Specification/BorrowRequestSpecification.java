package org.example.libraryms.Specification;

import org.example.libraryms.Entity.BorrowRequest;
import org.example.libraryms.Entity.BorrowRequestStatus;
import org.springframework.data.jpa.domain.Specification;


public class BorrowRequestSpecification {

    public static Specification<BorrowRequest> userIdEqual(Integer userId) {
        return (root, query, builder) -> builder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<BorrowRequest> statusEqual(String status) {
        return (root, query, builder) -> builder.equal(root.get("status"), BorrowRequestStatus.valueOf(status));
    }
}
