package com.canteen.repository;

import com.canteen.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder_OrderId(String orderId);

    @Query("SELECT oi.menuItem.name, SUM(oi.quantity) as total FROM OrderItem oi " +
           "WHERE DATE(oi.order.createdAt) = CURRENT_DATE AND oi.order.status = 'CLOSED' " +
           "GROUP BY oi.menuItem.name ORDER BY total DESC")
    List<Object[]> getDishWiseSalesToday();

    @Query("SELECT oi.menuItem.name, SUM(oi.quantity) as total FROM OrderItem oi " +
           "WHERE oi.order.createdAt BETWEEN :start AND :end AND oi.order.status = 'CLOSED' " +
           "GROUP BY oi.menuItem.name ORDER BY total DESC")
    List<Object[]> getDishWiseSalesForPeriod(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
