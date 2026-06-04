package com.canteen.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", unique = true, nullable = false)
    private String customerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "mobile_number", unique = true, nullable = false, length = 10)
    private String mobileNumber;

    @Column(name = "outstanding_balance")
    private Double outstandingBalance = 0.0;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @PrePersist
    public void prePersist() {
        this.registrationDate = LocalDateTime.now();
        if (this.outstandingBalance == null) this.outstandingBalance = 0.0;
    }
}
