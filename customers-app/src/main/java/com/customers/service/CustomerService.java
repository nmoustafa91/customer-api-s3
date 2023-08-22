package com.customers.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.customers.db.model.VersionModel;
import com.customers.model.CreateCustomerRequestDTO;
import com.customers.model.CustomerDTO;
import com.customers.model.ListCustomersResponseDTO;
import com.customers.model.UpdateCustomerRequestDTO;

public interface CustomerService {

  VersionModel<CustomerDTO> getCustomer(Long customerId);

  CustomerDTO createCustomer(CreateCustomerRequestDTO createCustomerRequestDTO);

  CustomerDTO updateCustomer(UpdateCustomerRequestDTO updateCustomerRequestDTO, Long customerId, String ifMatch);

  void deleteCustomer(Long customerId);

  ListCustomersResponseDTO getAllCustomers(PageRequest pageRequest);

  ListCustomersResponseDTO getCustomers(String firstName, String lastName, List<Long> customerIds, String city, String company,
      String product, String search, PageRequest pageRequest);
}
