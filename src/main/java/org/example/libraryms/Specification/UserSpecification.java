package org.example.libraryms.Specification;

import org.example.libraryms.Entity.Role;
import org.example.libraryms.Entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class UserSpecification {

    public static Specification<User> globalSearch(String keyword) {
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

    public static Specification<User> roleEqual(String role) {
        return ((root, query, builder) -> {
            return builder.equal(root.get("role"), Role.valueOf(role));
        });
    }

    public static Specification<User> registrationDateBetween(LocalDate from, LocalDate to) {
        return ((root, query, builder) -> {
            if (from != null && to != null) {
                return builder.between(root.get("registrationDate"), from, to);
            } else if (from != null) {
                return builder.greaterThanOrEqualTo(root.get("registrationDate"), from);
            } else if (to != null) {
                return builder.lessThanOrEqualTo(root.get("registrationDate"), to);
            }
            return builder.conjunction();
        });
    }
}
