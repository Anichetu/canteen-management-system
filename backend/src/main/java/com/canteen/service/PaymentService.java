package com.canteen.service;

import com.canteen.model.*;
import com.canteen.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final CustomerLedgerRepository customerLedgerRepository;

    @Transactional
    public Payment processPayment(String orderId, Double amountPaid, Payment.PaymentMethod method) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        Customer customer = order.getCustomer();

        double previousPending = customer.getOutstandingBalance();
        double totalDue = order.getTotalAmount() + previousPending;
        double pendingAfter = totalDue - amountPaid;
        if (pendingAfter < 0) pendingAfter = 0;

        Payment payment = Payment.builder()
                .order(order)
                .customer(customer)
                .amountPaid(amountPaid)
                .totalBill(order.getTotalAmount())
                .previousPending(previousPending)
                .pendingAmount(pendingAfter)
                .paymentMethod(method)
                .build();
        paymentRepository.save(payment);

        // Update customer outstanding balance
        customer.setOutstandingBalance(pendingAfter);
        customerRepository.save(customer);

        // Log to ledger
        CustomerLedger debit = CustomerLedger.builder()
                .customer(customer)
                .transactionType(CustomerLedger.TransactionType.DEBIT)
                .amount(order.getTotalAmount())
                .description("Order " + orderId)
                .balanceAfter(previousPending + order.getTotalAmount())
                .build();
        customerLedgerRepository.save(debit);

        CustomerLedger credit = CustomerLedger.builder()
                .customer(customer)
                .transactionType(CustomerLedger.TransactionType.CREDIT)
                .amount(amountPaid)
                .description("Payment via " + method + " for " + orderId)
                .balanceAfter(pendingAfter)
                .build();
        customerLedgerRepository.save(credit);

        // Close order if fully paid
        order.setStatus(Order.OrderStatus.CLOSED);
        order.setClosedAt(java.time.LocalDateTime.now());
        orderRepository.save(order);

        return payment;
    }

    public List<Payment> getPaymentsByCustomer(String customerId) {
        return paymentRepository.findByCustomer_CustomerId(customerId);
    }

    public List<Payment> getPaymentsByOrder(String orderId) {
        return paymentRepository.findByOrder_OrderId(orderId);
    }

    public Double getCashCollectionToday() { return paymentRepository.sumCashCollectionToday(); }
    public Double getUpiCollectionToday() { return paymentRepository.sumUpiCollectionToday(); }
    public Double getPendingCollectionToday() { return paymentRepository.sumPendingCollectionToday(); }

    public List<CustomerLedger> getLedgerByCustomer(String customerId) {
        return customerLedgerRepository.findByCustomer_CustomerIdOrderByTransactionDateDesc(customerId);
    }
}
