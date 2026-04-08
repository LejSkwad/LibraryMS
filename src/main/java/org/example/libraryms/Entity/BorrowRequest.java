package org.example.libraryms.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "borrow_requests")
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

    @OneToMany(mappedBy = "borrowRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowRequestItem> borrowRequestItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BorrowRequestStatus status;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @PrePersist
    public void prePersist() {
        this.requestDate = LocalDate.now();
        if(this.status == null) this.status = BorrowRequestStatus.PENDING;
    }
}
