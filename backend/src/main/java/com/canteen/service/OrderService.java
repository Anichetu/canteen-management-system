package com.canteen.service;

import com.canteen.model.*;
import com.canteen.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final MenuItemRepository menuItemRepository;
    private final CustomerLedgerRepository customerLedgerRepository;

    @Transactional
    public Order createOrder(String customerId, String waiterName) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
        String orderId = generateOrderId();
        Order order = Order.builder()
                .orderId(orderId)
                .customer(customer)
                .status(Order.OrderStatus.PENDING)
                .totalAmount(0.0)
                .waiterName(waiterName)
                .build();
        return orderRepository.save(order);
    }

    @Transactional
    public Order addItemToOrder(String orderId, Long menuItemId, Integer quantity) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + menuItemId));

        OrderItem item = OrderItem.builder()
                .order(order)
                .menuItem(menuItem)
                .quantity(quantity)
                .unitPrice(menuItem.getPrice())
                .subtotal(menuItem.getPrice() * quantity)
                .status(OrderItem.ItemStatus.PENDING)
                .build();
        orderItemRepository.save(item);

        recalculateOrderTotal(order);
        if (order.getStatus() == Order.OrderStatus.PENDING) {
            order.setStatus(Order.OrderStatus.IN_PROGRESS);
        }
        return orderRepository.save(order);
    }

    @Transactional
    public Order removeItemFromOrder(String orderId, Long orderItemId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        orderItemRepository.deleteById(orderItemId);
        recalculateOrderTotal(order);
        return orderRepository.save(order);
    }

    @Transactional
    public OrderItem updateItemStatus(Long orderItemId, OrderItem.ItemStatus status) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found: " + orderItemId));
        item.setStatus(status);
        return orderItemRepository.save(item);
    }

    public List<Order> getActiveOrders() {
        return orderRepository.findActiveOrders();
    }

    public Order getOrderById(String orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Transactional
    public Order cancelOrder(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setClosedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    private void recalculateOrderTotal(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrder_OrderId(order.getOrderId());
        double total = items.stream().mapToDouble(i -> i.getUnitPrice() * i.getQuantity()).sum();
        order.setTotalAmount(total);
    }

    private String generateOrderId() {
        int year = LocalDateTime.now().getYear();
        long count = orderRepository.count() + 1;
        return String.format("ORD-%d-%05d", year, count);
    }

    public Long countTodayOrders() { return orderRepository.countTodayOrders(); }
    public Long countTodayCompletedOrders() { return orderRepository.countTodayCompletedOrders(); }
    public Double sumTodayRevenue() { return orderRepository.sumTodayRevenue(); }

    public List<Object[]> getDishWiseSalesToday() {
        return orderItemRepository.getDishWiseSalesToday();
    }
}
