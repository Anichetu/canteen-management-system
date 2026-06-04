package com.canteen.service;

import com.canteen.model.Customer;
import com.canteen.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public Customer registerCustomer(String name, String mobileNumber) {
        if (customerRepository.existsByMobileNumber(mobileNumber)) {
            throw new RuntimeException("Customer with mobile number " + mobileNumber + " already exists.");
        }
        String customerId = generateCustomerId();
        Customer customer = Customer.builder()
                .customerId(customerId)
                .name(name)
                .mobileNumber(mobileNumber)
                .outstandingBalance(0.0)
                .build();
        return customerRepository.save(customer);
    }

    public Optional<Customer> findByMobile(String mobile) {
        return customerRepository.findByMobileNumber(mobile);
    }

    public Optional<Customer> findByCustomerId(String customerId) {
        return customerRepository.findByCustomerId(customerId);
    }

    public List<Customer> searchCustomers(String query) {
        return customerRepository.searchCustomers(query);
    }

    public List<Customer> getCustomersWithOutstandingBalance() {
        return customerRepository.findCustomersWithOutstandingBalance();
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Long countNewCustomersToday() {
        return customerRepository.countNewCustomersToday();
    }

    @Transactional
    public void updateOutstandingBalance(String customerId, Double newBalance) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
        customer.setOutstandingBalance(newBalance);
        customerRepository.save(customer);
    }

    private String generateCustomerId() {
        long count = customerRepository.count() + 1;
        return String.format("CUST-%06d", count);
    }
}
