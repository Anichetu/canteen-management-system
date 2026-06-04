package com.canteen.repository;

import com.canteen.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByCustomer_CustomerId(String customerId);

    List<Payment> findByOrder_OrderId(String orderId);

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p WHERE DATE(p.paymentDate) = CURRENT_DATE AND p.paymentMethod = 'CASH'")
    Double sumCashCollectionToday();

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p WHERE DATE(p.paymentDate) = CURRENT_DATE AND p.paymentMethod = 'GPAY_UPI'")
    Double sumUpiCollectionToday();

    @Query("SELECT COALESCE(SUM(p.pendingAmount), 0) FROM Payment p WHERE DATE(p.paymentDate) = CURRENT_DATE")
    Double sumPendingCollectionToday();
}
