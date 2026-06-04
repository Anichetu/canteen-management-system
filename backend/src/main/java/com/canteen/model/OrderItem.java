package com.canteen.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @Column(name = "subtotal")
    private Double subtotal;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ItemStatus status = ItemStatus.PENDING;

    @PrePersist
    @PreUpdate
    public void calculateSubtotal() {
        this.subtotal = this.unitPrice * this.quantity;
        if (this.status == null) this.status = ItemStatus.PENDING;
    }

    public enum ItemStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }
}
