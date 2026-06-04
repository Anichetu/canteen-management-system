package com.canteen.repository;

import com.canteen.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByMobileNumber(String mobileNumber);

    Optional<Customer> findByCustomerId(String customerId);

    @Query("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT(:query, '%')) OR c.mobileNumber LIKE CONCAT(:query, '%') OR c.customerId LIKE CONCAT(:query, '%')")
    List<Customer> searchCustomers(@Param("query") String query);

    boolean existsByMobileNumber(String mobileNumber);

    @Query("SELECT COUNT(c) FROM Customer c WHERE DATE(c.registrationDate) = CURRENT_DATE")
    Long countNewCustomersToday();

    @Query("SELECT c FROM Customer c WHERE c.outstandingBalance > 0")
    List<Customer> findCustomersWithOutstandingBalance();
}
