package com.customers.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.customers.db.model.Contract;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {

  Page<Contract> findAllByCustomerCustomerId(Long customerId, PageRequest pageRequest);

  void deleteAllByCustomerCustomerId(Long customerId);
}
