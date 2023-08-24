package com.customers.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import com.customers.CustomersApplication;
import com.customers.db.repository.CustomerRepository;
import com.customers.integration.helper.CustomersHelper;
import com.customers.model.ContractDetailsDTO;
import com.customers.model.CreateContractsRequestDTO;
import com.customers.model.CreateCustomerRequestDTO;
import com.customers.model.CustomerDTO;

@SpringBootTest(classes = CustomersApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "PT30S")
public class AbstractIT {

    public static final CreateCustomerRequestDTO CUSTOMER_01 = new CreateCustomerRequestDTO().firstName("FirstName_01").lastName("LastName_01")
        .street("street").streetAdditional("01").city("Berlin").land("Germany").postal("12345").email("customer01@test.com")
        .companyName("company");
    public static final CreateCustomerRequestDTO CUSTOMER_02 = new CreateCustomerRequestDTO().firstName("FirstName_02").lastName("LastName_02")
        .street("street").streetAdditional("01").city("Munich").land("Germany").postal("12344").email("customer02@test.com")
        .companyName("company");
    public static final CreateCustomerRequestDTO CUSTOMER_03 = new CreateCustomerRequestDTO().firstName("FirstName_03").lastName("LastName_03")
        .street("street").streetAdditional("01").city("Hamburg").land("Germany").postal("12344").email("customer03@test.com")
        .companyName("company");
    public static final CreateCustomerRequestDTO CUSTOMER_04 = new CreateCustomerRequestDTO().firstName("FirstName_04").lastName("LastName_04")
        .street("street").streetAdditional("01").city("Berlin").land("Germany").postal("12345").email("customer04@test.com")
        .companyName("company");
    public static final CreateCustomerRequestDTO CUSTOMER_05 = new CreateCustomerRequestDTO().firstName("Alrik").lastName("Kuzel")
        .street("street").streetAdditional("01").city("Berlin").land("Germany").postal("12345").email("customer05@test.com")
        .companyName("company");

    public static final ContractDetailsDTO CONTRACT_11 = new ContractDetailsDTO().contractId("CONTRACT_11").productNumber("PRODUCT_11");
    public static final ContractDetailsDTO CONTRACT_12 = new ContractDetailsDTO().contractId("CONTRACT_12").productNumber("PRODUCT_12");
    public static final ContractDetailsDTO CONTRACT_13 = new ContractDetailsDTO().contractId("CONTRACT_13").productNumber("PRODUCT_13");
    public static final CreateContractsRequestDTO CONTRACTS_1 = new CreateContractsRequestDTO().contracts
        (List.of(CONTRACT_11, CONTRACT_12, CONTRACT_13)
    );

    public static final ContractDetailsDTO CONTRACT_31 = new ContractDetailsDTO().contractId("CONTRACT_31").productNumber("PRODUCT_31");
    public static final CreateContractsRequestDTO CONTRACTS_3 = new CreateContractsRequestDTO().contracts
        (List.of(CONTRACT_11, CONTRACT_12, CONTRACT_31)
        );

    public static final ContractDetailsDTO CONTRACT_51 = new ContractDetailsDTO().contractId("CONTRACT_51").productNumber("PRODUCT_51");
    public static final ContractDetailsDTO CONTRACT_52 = new ContractDetailsDTO().contractId("CONTRACT_52").productNumber("PRODUCT_52");
     public static final CreateContractsRequestDTO CONTRACTS_5 = new CreateContractsRequestDTO().contracts
        (List.of(CONTRACT_51, CONTRACT_52)
        );

    @Autowired
    protected TestRestTemplate restTemplate;
    @Autowired
    protected CustomerRepository customerRepository;

    protected CustomersHelper customersHelper;

    @BeforeEach
    protected void setUp() {
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        customersHelper = new CustomersHelper(restTemplate);
    }

    protected void assertCreatedCustomer(CustomerDTO created) {
        assertEquals(CUSTOMER_01.getLastName(), created.getLastName());
        assertEquals(CUSTOMER_01.getFirstName(), created.getFirstName());
        assertEquals(CUSTOMER_01.getStreet(), created.getStreet());
        assertEquals(CUSTOMER_01.getStreetAdditional(), created.getStreetAdditional());
        assertEquals(CUSTOMER_01.getCity(), created.getCity());
        assertEquals(CUSTOMER_01.getLand(), created.getLand());
        assertEquals(CUSTOMER_01.getPostal(), created.getPostal());
        assertEquals(CUSTOMER_01.getEmail(), created.getEmail());
        assertEquals(CUSTOMER_01.getCompanyName(), created.getCompanyName());
        assertNotNull(created.getCreated());
        assertNotNull(created.getCreatedBy());
    }

}
