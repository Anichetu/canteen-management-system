package com.canteen.controller;

import com.canteen.model.Order;
import com.canteen.model.OrderItem;
import com.canteen.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // POST /api/orders/create
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, String> body) {
        try {
            String customerId = body.get("customerId");
            String waiterName = body.getOrDefault("waiterName", "");
            Order order = orderService.createOrder(customerId, waiterName);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/orders/{orderId}/items
    @PostMapping("/{orderId}/items")
    public ResponseEntity<?> addItemToOrder(
            @PathVariable String orderId,
            @RequestBody Map<String, Object> body) {
        try {
            Long menuItemId = Long.valueOf(body.get("menuItemId").toString());
            Integer quantity = Integer.valueOf(body.get("quantity").toString());
            Order order = orderService.addItemToOrder(orderId, menuItemId, quantity);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/orders/{orderId}/items/{itemId}
    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<?> removeItem(
            @PathVariable String orderId,
            @PathVariable Long itemId) {
        try {
            Order order = orderService.removeItemFromOrder(orderId, itemId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PATCH /api/orders/items/{itemId}/status
    @PatchMapping("/items/{itemId}/status")
    public ResponseEntity<?> updateItemStatus(
            @PathVariable Long itemId,
            @RequestBody Map<String, String> body) {
        try {
            OrderItem.ItemStatus status = OrderItem.ItemStatus.valueOf(body.get("status"));
            OrderItem item = orderService.updateItemStatus(itemId, status);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/orders/active
    @GetMapping("/active")
    public ResponseEntity<List<Order>> getActiveOrders() {
        return ResponseEntity.ok(orderService.getActiveOrders());
    }

    // GET /api/orders/{orderId}
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable String orderId) {
        try {
            return ResponseEntity.ok(orderService.getOrderById(orderId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/orders/customer/{customerId}
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    // PATCH /api/orders/{orderId}/cancel
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId) {
        try {
            return ResponseEntity.ok(orderService.cancelOrder(orderId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
