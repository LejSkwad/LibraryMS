package org.example.libraryms.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "borrow_request")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "borrowRequest")
    private List<BorrowRequestItem> borrowRequestItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BorrowRequestStatus status;

    private LocalDate createDate;

    @PrePersist
    public void prePersist() {
        this.createDate = LocalDate.now();
        if(this.status == null) this.status = BorrowRequestStatus.PENDING;
    }
}
