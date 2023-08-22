package com.customers.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.customers.api.CustomersApi;
import com.customers.db.model.VersionModel;
import com.customers.etag.CustomerETagResponseEntity;
import com.customers.model.CreateCustomerRequestDTO;
import com.customers.model.CustomerDTO;
import com.customers.model.ListCustomersResponseDTO;
import com.customers.model.UpdateCustomerRequestDTO;
import com.customers.service.CustomerService;

import lombok.RequiredArgsConstructor;

/**
 * This is the controller layer to handle the client request for the customers api.
 * It implements customers Api interface which is generated already using open api generation, so wo don't have to specify everything explicitly
 * in the code by annotations, but I prepared the open api and the DTOs and the API were generated based on what I have defined in openapi yaml file.
 */
@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

	private final CustomerService customerservice;

	@Override
	public ResponseEntity<CustomerDTO> createCustomer(CreateCustomerRequestDTO createCustomerRequestDTO) {
		CustomerDTO customerDTO = customerservice.createCustomer(createCustomerRequestDTO);

		return ResponseEntity.status(HttpStatus.CREATED).body(customerDTO);
	}

	/**
	 *
	 * It is a general filtering endpoint to facilitate the searching and filtering to the end users, it could be extended but I just provided a simple version.
	 * Pagination is considered
	 *
	 * @param firstName Customer first name query parameter (optional)
	 * @param lastName Customer last name query parameter (optional)
	 * @param customerIds Customers ids query parameter, can be comma-separated list to include multiple values (optional)
	 * @param city Customer city query parameter (optional)
	 * @param company Company name query parameter (optional)
	 * @param product Product number query parameter (optional)
	 * @param search Provides full text search on Customers.  Searches in following parameters:   * id   * first name  (optional)
	 * @param pageNumber Page number, default is 0 (optional, default to 0)
	 * @param pageSize Number of items in a page, default page size is 20, maximum 50 (optional, default to 20)
	 * @param sort Sort criteria, format: &#39;?sort&#x3D;&amp;lt;propertyA&amp;gt;[,&amp;lt;propertyB&amp;gt;][,(asc|desc)]&#39;, sort parameter can be used several times in one query  (optional)
	 * @return
	 */
	@Override
	public ResponseEntity<ListCustomersResponseDTO> getCustomers(String firstName, String lastName, List<Long> customerIds,
			String city, String company, String product, String search, Integer pageNumber, Integer pageSize, String sort) {
		ListCustomersResponseDTO listCustomersResponseDTO = customerservice.getCustomers(firstName, lastName, customerIds,
				city, company, product, search, PageRequest.of(pageNumber, pageSize, Sort.by(sort == null ? "created" : sort)));
		return ResponseEntity.ok(listCustomersResponseDTO);
	}

	@Override
	public ResponseEntity<Void> deleteCustomer(Long customerId) {
		customerservice.deleteCustomer(customerId);

		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<ListCustomersResponseDTO> getAllCustomers(Integer pageNumber, Integer pageSize, String sort) {
		ListCustomersResponseDTO listCustomersResponseDTO = customerservice.getAllCustomers(PageRequest.of(pageNumber, pageSize, Sort.by(sort == null ? "created" : sort)));
		return ResponseEntity.ok(listCustomersResponseDTO);
	}

	@Override
	public ResponseEntity<CustomerDTO> getCustomer(Long customerId) {
		VersionModel<CustomerDTO> customerDTOVersionModel = customerservice.getCustomer(customerId);

		return new CustomerETagResponseEntity<>(customerDTOVersionModel).ok();

	}

	@Override
	public ResponseEntity<CustomerDTO> updateCustomer(Long customerId, String ifMatch, UpdateCustomerRequestDTO updateCustomerRequestDTO) {
		CustomerDTO customerDTO = customerservice.updateCustomer(updateCustomerRequestDTO, customerId, ifMatch);
		return ResponseEntity.ok(customerDTO);
	}

}