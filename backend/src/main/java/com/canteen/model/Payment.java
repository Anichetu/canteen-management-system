package com.canteen.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid;

    @Column(name = "total_bill")
    private Double totalBill;

    @Column(name = "previous_pending")
    private Double previousPending = 0.0;

    @Column(name = "pending_amount")
    private Double pendingAmount = 0.0;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @PrePersist
    public void prePersist() {
        this.paymentDate = LocalDateTime.now();
    }

    public enum PaymentMethod {
        CASH, GPAY_UPI
    }
}
