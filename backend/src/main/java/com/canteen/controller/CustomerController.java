package com.canteen.controller;

import com.canteen.model.Customer;
import com.canteen.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // POST /api/customers/register
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody Map<String, String> body) {
        try {
            String name = body.get("name");
            String mobile = body.get("mobileNumber");
            Customer customer = customerService.registerCustomer(name, mobile);
            return ResponseEntity.ok(customer);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/customers/search?query=RA
    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String query) {
        return ResponseEntity.ok(customerService.searchCustomers(query));
    }

    // GET /api/customers/mobile/{mobile}
    @GetMapping("/mobile/{mobile}")
    public ResponseEntity<?> getByMobile(@PathVariable String mobile) {
        Optional<Customer> customer = customerService.findByMobile(mobile);
        return customer.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/customers/{customerId}
    @GetMapping("/{customerId}")
    public ResponseEntity<?> getByCustomerId(@PathVariable String customerId) {
        Optional<Customer> customer = customerService.findByCustomerId(customerId);
        return customer.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/customers
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // GET /api/customers/outstanding
    @GetMapping("/outstanding")
    public ResponseEntity<List<Customer>> getCustomersWithOutstanding() {
        return ResponseEntity.ok(customerService.getCustomersWithOutstandingBalance());
    }
}
