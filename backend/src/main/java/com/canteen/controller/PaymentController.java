package com.canteen.controller;

import com.canteen.model.Customer;
import com.canteen.model.CustomerLedger;
import com.canteen.model.Payment;
import com.canteen.service.CustomerService;
import com.canteen.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final CustomerService customerService;

    // POST /api/payments/process
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody Map<String, Object> body) {
        try {
            String orderId = body.get("orderId").toString();
            Double amountPaid = Double.valueOf(body.get("amountPaid").toString());
            Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(body.get("paymentMethod").toString());
            Payment payment = paymentService.processPayment(orderId, amountPaid, method);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/payments/customer/{customerId}
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Payment>> getByCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByCustomer(customerId));
    }

    // GET /api/payments/order/{orderId}
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Payment>> getByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrder(orderId));
    }

    // GET /api/payments/ledger/{customerId}
    @GetMapping("/ledger/{customerId}")
    public ResponseEntity<List<CustomerLedger>> getLedger(@PathVariable String customerId) {
        return ResponseEntity.ok(paymentService.getLedgerByCustomer(customerId));
    }

    // GET /api/payments/billing/{customerId} — get current bill + outstanding
    @GetMapping("/billing/{customerId}")
    public ResponseEntity<?> getBillingInfo(@PathVariable String customerId) {
        Customer customer = customerService.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return ResponseEntity.ok(Map.of(
                "customerId", customer.getCustomerId(),
                "customerName", customer.getName(),
                "outstandingBalance", customer.getOutstandingBalance()
        ));
    }
}
