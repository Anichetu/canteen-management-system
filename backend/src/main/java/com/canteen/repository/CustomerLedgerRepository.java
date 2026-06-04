package com.canteen.repository;

import com.canteen.model.CustomerLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerLedgerRepository extends JpaRepository<CustomerLedger, Long> {
    List<CustomerLedger> findByCustomer_CustomerIdOrderByTransactionDateDesc(String customerId);
}
