package com.canteen.repository;

import com.canteen.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderId(String orderId);

    List<Order> findByCustomer_CustomerIdAndStatusNot(String customerId, Order.OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING','IN_PROGRESS') ORDER BY o.createdAt DESC")
    List<Order> findActiveOrders();

    @Query("SELECT o FROM Order o WHERE o.customer.customerId = :customerId ORDER BY o.createdAt DESC")
    List<Order> findByCustomerId(@Param("customerId") String customerId);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    List<Order> findOrdersBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE DATE(o.createdAt) = CURRENT_DATE")
    Long countTodayOrders();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'CLOSED' AND DATE(o.createdAt) = CURRENT_DATE")
    Long countTodayCompletedOrders();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'CLOSED' AND DATE(o.createdAt) = CURRENT_DATE")
    Double sumTodayRevenue();
}
