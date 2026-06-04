package com.canteen.controller;

import com.canteen.service.OrderService;
import com.canteen.service.PaymentService;
import com.canteen.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final CustomerService customerService;

    // GET /api/reports/daily — Full end-of-day / live report
    @GetMapping("/daily")
    public ResponseEntity<Map<String, Object>> getDailyReport() {
        Map<String, Object> report = new LinkedHashMap<>();

        // Revenue summary
        Map<String, Object> revenue = new LinkedHashMap<>();
        double cash = paymentService.getCashCollectionToday();
        double upi = paymentService.getUpiCollectionToday();
        double pending = paymentService.getPendingCollectionToday();
        double total = orderService.sumTodayRevenue();
        revenue.put("totalSales", total);
        revenue.put("cashCollection", cash);
        revenue.put("gpayCollection", upi);
        revenue.put("pendingCollection", pending);
        report.put("revenue", revenue);

        // Order summary
        Map<String, Object> orders = new LinkedHashMap<>();
        orders.put("totalOrders", orderService.countTodayOrders());
        orders.put("completedOrders", orderService.countTodayCompletedOrders());
        report.put("orders", orders);

        // Customer summary
        Map<String, Object> customers = new LinkedHashMap<>();
        customers.put("newCustomersToday", customerService.countNewCustomersToday());
        customers.put("customersWithOutstanding", customerService.getCustomersWithOutstandingBalance().size());
        report.put("customers", customers);

        // Dish wise sales
        List<Object[]> rawDish = orderService.getDishWiseSalesToday();
        List<Map<String, Object>> dishSales = new ArrayList<>();
        for (Object[] row : rawDish) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("dish", row[0]);
            entry.put("quantity", row[1]);
            dishSales.add(entry);
        }
        report.put("dishWiseSales", dishSales);

        return ResponseEntity.ok(report);
    }

    // GET /api/reports/dashboard — Quick live stats for owner dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dash = new LinkedHashMap<>();
        dash.put("todayRevenue", orderService.sumTodayRevenue());
        dash.put("todayOrders", orderService.countTodayOrders());
        dash.put("cashCollection", paymentService.getCashCollectionToday());
        dash.put("upiCollection", paymentService.getUpiCollectionToday());
        dash.put("pendingCollection", paymentService.getPendingCollectionToday());
        dash.put("newCustomers", customerService.countNewCustomersToday());
        return ResponseEntity.ok(dash);
    }
}
