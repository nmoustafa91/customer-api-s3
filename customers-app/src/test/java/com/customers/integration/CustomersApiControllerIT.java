package com.customers.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import com.customers.model.CreateCustomerRequestDTO;
import com.customers.model.UpdateCustomerRequestDTO;

public class CustomersApiControllerIT extends AbstractIT {

	@BeforeEach
	void init() {
		customerRepository.deleteAll();
	}

	@Test
	void createCustomer() {
		var response = customersHelper.create(CUSTOMER_01);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var customerDTO = response.getBody();
		assertCreatedCustomer(customerDTO);
	}

	@Test
	void createCustomer_withAnExistingEmail_shouldFail() {
		var response = customersHelper.create(CUSTOMER_02);

		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

		var request = new CreateCustomerRequestDTO().email(CUSTOMER_02.getEmail()).firstName("name").land("last")
				.street("street").streetAdditional("01").city("Hamburg").land("Germany").postal("12344")
				.companyName("company");
		response = customersHelper.create(request);
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}

	@Test
	void updateCustomer_notFound() {
		var request = new UpdateCustomerRequestDTO().street("street_").streetAdditional("01").city("Hamburg").land("Germany").postal("12344").email("customer03@test.com")
				.companyName("company_1");
		customersHelper.update(request, 12L, HttpStatus.NOT_FOUND);
	}

	@Test
	void updateCustomer_withExistingCustomerCommunication_shouldFail() {
		customersHelper.create(CUSTOMER_03);
		var response = customersHelper.create(CUSTOMER_04);
		var customer = response.getBody();
		var updateRequest = new UpdateCustomerRequestDTO().street("street_up").streetAdditional("01")
				.city("Hamburg").land("Germany").postal("12344").email(CUSTOMER_03.getEmail())
				.companyName("company").firstName(CUSTOMER_03.getFirstName()).lastName(CUSTOMER_03.getLastName());
		customersHelper.update(updateRequest, customer.getCustomerId(), HttpStatus.BAD_REQUEST);
	}

	@Test
	void updateCustomerAddress() {
		var response = customersHelper.create(CUSTOMER_05);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var createdCustomer = response.getBody();
		var updateRequest = new UpdateCustomerRequestDTO().street("updatedStr")
				.streetAdditional("88").city("Plauen").postal("1223");
		customersHelper.update(updateRequest, createdCustomer.getCustomerId());
		response = customersHelper.getCustomer(createdCustomer.getCustomerId());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		var updatedCustomer = response.getBody();
		assertThat(updatedCustomer.getFirstName(), is(createdCustomer.getFirstName()));
		assertThat(updatedCustomer.getLastName(), is(createdCustomer.getLastName()));
		assertThat(updatedCustomer.getEmail(), is(createdCustomer.getEmail()));
		assertThat(updatedCustomer.getLand(), is(createdCustomer.getLand()));
		assertThat(updatedCustomer.getCompanyName(), is(createdCustomer.getCompanyName()));

		assertThat(updatedCustomer.getStreet(), is(updateRequest.getStreet()));
		assertThat(updatedCustomer.getStreetAdditional(), is(updateRequest.getStreetAdditional()));
		assertThat(updatedCustomer.getCity(), is(updateRequest.getCity()));
		assertThat(updatedCustomer.getPostal(), is(updateRequest.getPostal()));

		assertNotNull(updatedCustomer.getCreated());
		assertNotNull(updatedCustomer.getCreatedBy());
		assertNotNull(updatedCustomer.getLastChange());
		assertNotNull(updatedCustomer.getLastChangeBy());
	}

	@Test
	void updateCustomerPersonalInformation() {
		var response = customersHelper.create(CUSTOMER_01);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var createdCustomer = response.getBody();
		var updateRequest = new UpdateCustomerRequestDTO().firstName("updated").email("updated123@test.com");
		customersHelper.update(updateRequest, createdCustomer.getCustomerId());
		response = customersHelper.getCustomer(createdCustomer.getCustomerId());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		var updatedCustomer = response.getBody();
		assertThat(updatedCustomer.getLastName(), is(createdCustomer.getLastName()));
		assertThat(updatedCustomer.getLand(), is(createdCustomer.getLand()));
		assertThat(updatedCustomer.getCompanyName(), is(createdCustomer.getCompanyName()));
		assertThat(updatedCustomer.getStreet(), is(createdCustomer.getStreet()));
		assertThat(updatedCustomer.getStreetAdditional(), is(createdCustomer.getStreetAdditional()));
		assertThat(updatedCustomer.getCity(), is(createdCustomer.getCity()));
		assertThat(updatedCustomer.getPostal(), is(createdCustomer.getPostal()));

		assertThat(updatedCustomer.getFirstName(), is(updateRequest.getFirstName()));
		assertThat(updatedCustomer.getEmail(), is(updateRequest.getEmail()));

		assertNotNull(updatedCustomer.getCreated());
		assertNotNull(updatedCustomer.getCreatedBy());
		assertNotNull(updatedCustomer.getLastChange());
		assertNotNull(updatedCustomer.getLastChangeBy());
	}

	@Test
	void getCustomer_notFound() {
		customersHelper.getCustomer(1000L, HttpStatus.NOT_FOUND);
	}

	@Test
	void getCustomer() {
		var response = customersHelper.create(CUSTOMER_01);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var customerDTO = response.getBody();
		response = customersHelper.getCustomer(customerDTO.getCustomerId());
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		var getCustomerResponse = response.getBody();

		assertCreatedCustomer(getCustomerResponse);
		assertNotNull(getCustomerResponse.getCreated());
		assertNotNull(getCustomerResponse.getCreatedBy());

	}

	@Test
	void deleteCustomer_not_found() {
		var response = customersHelper.create(CUSTOMER_04);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
		var customerDTO = response.getBody();
		customersHelper.deleteCustomer(customerDTO.getCustomerId());
		assertFalse(customerRepository.existsById(customerDTO.getCustomerId()));
	}

	@Test
	void deleteCustomer() {
		customersHelper.deleteCustomer(10L, HttpStatus.NOT_FOUND);
	}

	@Test
	void listCustomers_Unfiltered() {
		prepareTestData();

		var response = customersHelper.getCustomers(null, null, null, null, null, null, null, null);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(5, response.getBody().getResults().size());
	}

	@Test
	void listCustomers_filteredByName() {
		prepareTestData();

		var response = customersHelper.getCustomers("NAME", null, null,
				null, null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(4, response.getBody().getResults().size());
		response = customersHelper.getCustomers("FirstName_01", null, null,
				null, null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(1, response.getBody().getResults().size());
		response = customersHelper.getCustomers("MARTIN", null, null,
				null, null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(0, response.getBody().getResults().size());
	}

	@Test
	void listCustomers_filteredByCustomerCity() {
		prepareTestData();

		var response = customersHelper.getCustomers(null, null, null,
				"CITY", null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(0, response.getBody().getResults().size());
		response = customersHelper.getCustomers(null, null, null,
				"Berlin", null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(3, response.getBody().getResults().size());
		response = customersHelper.getCustomers("", null, null,
				"Munich", null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(1, response.getBody().getResults().size());
	}

	@Test
	void listCustomers_filteredByCustomerCompany() {
		prepareTestData();

		var response = customersHelper.getCustomers(null, null, null,
				null, "company", null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(5, response.getBody().getResults().size());
	}
	
	@Test
	void listCustomers_filterByCustomerIds() {
		final List<String> customerIds = prepareTestData();

		var response = customersHelper.getCustomers(null, null, customerIds,
				null, null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(5, response.getBody().getResults().size());

		response = customersHelper.getCustomers(null, null, List.of(customerIds.get(0), customerIds.get(2)),
				null, null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(2, response.getBody().getResults().size());

		response = customersHelper.getCustomers(null, null, List.of(String.valueOf(55L)),
				null, null, null, null, PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(0, response.getBody().getResults().size());
	}

	@Test
	void listCustomers_filterBySearch() {
		final List<String> customerIds = prepareTestData();

		var response = customersHelper.getCustomers(null, null, null,
				null, null, null, "NAME", PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(4, response.getBody().getResults().size());

		response = customersHelper.getCustomers(null, null, null,
				null, null, null, customerIds.get(0), PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(1, response.getBody().getResults().size());

		response = customersHelper.getCustomers(null, null, null,
				null, null, null, "xyz", PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(0, response.getBody().getResults().size());
	}

	@Test
	void getAllCustomers() {
		prepareTestData();

		var response = customersHelper.getAllCustomers(PageRequest.of(0, 10));
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertEquals(5, response.getBody().getResults().size());
	}

	private List<String> prepareTestData() {
		var customer1 = customersHelper.create(CUSTOMER_01).getBody();
		var customer2 = customersHelper.create(CUSTOMER_02).getBody();
		var customer3 = customersHelper.create(CUSTOMER_03).getBody();
		var customer4 = customersHelper.create(CUSTOMER_04).getBody();
		var customer5 = customersHelper.create(CUSTOMER_05).getBody();

		return List.of(String.valueOf(customer1.getCustomerId()), String.valueOf(customer2.getCustomerId()),
				String.valueOf(customer3.getCustomerId()), String.valueOf(customer4.getCustomerId()), String.valueOf(customer5.getCustomerId()));
	}
}
