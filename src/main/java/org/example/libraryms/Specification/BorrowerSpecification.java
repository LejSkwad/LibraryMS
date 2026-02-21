package org.example.libraryms.Specification;

import org.example.libraryms.Entity.Borrower;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class BorrowerSpecification {
    public static Specification<Borrower> globalSearch(String keyword) {
        return ((root, query, builder) -> {
            return builder.or(
                    builder.like(builder.lower(root.get("firstName")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("lastName")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("phone")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("address")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("socialNumber")), "%" + keyword.toLowerCase() + "%")
            );
        });
    }

    public static Specification<Borrower> registrationDateBetween (LocalDate registrationDateFrom, LocalDate registrationDateTo) {
        return((root, query, builder) -> {
            if(registrationDateFrom != null && registrationDateTo != null){
                return builder.between(root.get("registrationDate"), registrationDateFrom, registrationDateTo);
            } else if(registrationDateFrom != null){
                return builder.greaterThanOrEqualTo(root.get("registrationDate"), registrationDateFrom);
            } else if(registrationDateTo != null){
                return builder.lessThanOrEqualTo(root.get("registrationDate"), registrationDateTo);
            }
            else return builder.conjunction();
        });
    }
}
