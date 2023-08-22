package com.customers.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.customers.db.model.Customer;
import com.customers.db.model.VersionModel;
import com.customers.db.repository.CustomerRepository;
import com.customers.db.repository.CustomerSpecificationHelper;
import com.customers.domain.CustomerFilter;
import com.customers.etag.utils.ETagUtils;
import com.customers.exception.NotFoundException;
import com.customers.exception.ValidationException;
import com.customers.exception.general.ApplicationError;
import com.customers.exception.general.ErrorCode;
import com.customers.mapper.CustomerMapper;
import com.customers.model.CreateCustomerRequestDTO;
import com.customers.model.CustomerDTO;
import com.customers.model.ListCustomersResponseDTO;
import com.customers.model.UpdateCustomerRequestDTO;
import com.customers.service.CustomerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

  public static final List<String> DETAIL_HEADERS = List.of("Firma", "Strasse", "Strassenzusatz", "Ort", "Land", "PLZ",
      "Vorname", "Nachname", "Kunden-ID");

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  @Override
  @Transactional
  public VersionModel<CustomerDTO> getCustomer(Long customerId) {
    Customer customer = getCustomerById(customerId);

    return customerMapper.entityToVersionModel(customer);
  }

  @Override
  @Transactional
  public ListCustomersResponseDTO getCustomers(String firstName, String lastName, List<Long> customerIds,
      String city, String company, String product, String search, PageRequest pageRequest) {
    final CustomerFilter filter = CustomerFilter.builder().firstName(firstName).lastName(lastName).customersIds(customerIds)
        .city(city).companyName(company).productNumber(product).search(search)
        .build();

    Page<Customer> page = customerRepository.findAll(CustomerSpecificationHelper.createFilter(filter), pageRequest);
    return customerMapper.pageToCustomersResponseDTO(page);
  }

  @Override
  public CustomerDTO createCustomer(CreateCustomerRequestDTO createCustomerRequestDTO) {
    Customer customer = customerMapper.fromCreateBodyToEntity(createCustomerRequestDTO);

    validateCustomerCreate(createCustomerRequestDTO);

    return customerMapper.fromEntity(customerRepository.save(customer));
  }

  @Override
  @Transactional
  public CustomerDTO updateCustomer(UpdateCustomerRequestDTO updateCustomerRequestDTO, Long customerId,
      String ifMatch) {
    Customer customer = getCustomerById(customerId);

    ETagUtils.checkETag(customer, ifMatch);
    validateCustomerUpdate(updateCustomerRequestDTO, customer);

    customer = customerMapper.updateEntityFromModel(updateCustomerRequestDTO, customer);

    return customerMapper.fromEntity(customerRepository.save(customer));
  }

  @Override
  public void deleteCustomer(Long customerId) {
    Customer customer = getCustomerById(customerId);
    customerRepository.delete(customer);
  }

  @Override
  public ListCustomersResponseDTO getAllCustomers(PageRequest pageRequest) {
    Page<Customer> page = customerRepository.findAll(pageRequest);
    return customerMapper.pageToCustomersResponseDTO(page);
  }

  private void validateCustomerUpdate(UpdateCustomerRequestDTO updateCustomerRequestDTO, Customer customer) {
    if (!Objects.equals(customer.getEmail(), updateCustomerRequestDTO.getEmail()) &&
        customerRepository.existsByEmail(updateCustomerRequestDTO.getEmail())) {
      throw new ValidationException(new ApplicationError()
          .setParameters(List.of(updateCustomerRequestDTO.getEmail()))
          .setCodeAndMessage(ErrorCode.CUSTOMER_EMAIL_ALREADY_EXISTS));
    }

  }

  private void validateCustomerCreate(CreateCustomerRequestDTO createCustomerRequestDTO) {
    if (customerRepository.existsByEmail(createCustomerRequestDTO.getEmail())) {
      throw new ValidationException(new ApplicationError()
          .setParameters(List.of(createCustomerRequestDTO.getEmail()))
          .setCodeAndMessage(ErrorCode.CUSTOMER_EMAIL_ALREADY_EXISTS));
    }
  }

  private Customer getCustomerById(Long customerId) {
    return customerRepository.findById(customerId).orElseThrow(
        () -> new NotFoundException(new ApplicationError()
            .setParameters(List.of(customerId))
            .setCodeAndMessage(ErrorCode.CUSTOMER_NOT_FOUND)));
  }
}
