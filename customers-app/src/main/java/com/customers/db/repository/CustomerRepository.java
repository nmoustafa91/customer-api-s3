package com.customers.db.repository;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.customers.db.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>,
    JpaSpecificationExecutor<Customer> {

  boolean existsByEmail(String email);

  @Query("SELECT DISTINCT c FROM Customer c WHERE c.created > :deltaDate OR  c.lastChange > :deltaDate OR "
      + "c.customerId IN "
      + "(SELECT DISTINCT t.customer.customerId FROM Contract t WHERE t.created > :deltaDate OR  t.lastChange > :deltaDate)")
  Slice<Customer> getDeltaCustomers(OffsetDateTime deltaDate, Pageable pageable);
}
