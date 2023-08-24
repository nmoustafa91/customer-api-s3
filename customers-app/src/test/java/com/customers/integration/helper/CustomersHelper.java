package com.customers.integration.helper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import com.customers.model.CreateCustomerRequestDTO;
import com.customers.model.CustomerDTO;
import com.customers.model.ListCustomersResponseDTO;
import com.customers.model.UpdateCustomerRequestDTO;

public class CustomersHelper extends AbstractRestCallHelper {

  private static final String CUSTOMERS_URL = "/customers";
  private static final String CUSTOMER_URL = "/customers/{customerId}";
  private static final String ALL_CUSTOMERS_URL = "/customers/all-customers";

  public CustomersHelper(TestRestTemplate testRestTemplate) {
    super(testRestTemplate);
  }

  public ResponseEntity<CustomerDTO> create(CreateCustomerRequestDTO request) {
    return testRestTemplate.postForEntity(CUSTOMERS_URL, request, CustomerDTO.class);
  }

  public ResponseEntity<CustomerDTO> getCustomer(Long customerId) {
    return testRestTemplate.getForEntity(CUSTOMER_URL, CustomerDTO.class, customerId);
  }

  public void getCustomer(long customerId, HttpStatus expectedStatus) {
    var response = testRestTemplate.getForEntity(CUSTOMER_URL, Object.class, customerId);
    assertThat(response.getStatusCode(), is(expectedStatus));
  }

  public void update(UpdateCustomerRequestDTO request, Long customerId) {
    String version = getCustomer(customerId).getHeaders().getETag();
    HttpHeaders headers = new HttpHeaders();
    headers.add("If-Match", version);
    testRestTemplate
        .exchange(CUSTOMER_URL, HttpMethod.PUT, new HttpEntity<>(request, headers), CustomerDTO.class, customerId);
  }

  public void update(UpdateCustomerRequestDTO request, long customerId, HttpStatus expectedStatus) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("If-Match", "\"0\"");
    var response = testRestTemplate.exchange(CUSTOMER_URL, HttpMethod.PUT, new HttpEntity<>(request, headers), Object.class, customerId);
    assertThat(response.getStatusCode(), is(expectedStatus));
  }

  public ResponseEntity<ListCustomersResponseDTO> getCustomers(String firstName, String lastName, List<String> customerIds,
      String city, String company, String product, String search, Pageable pageRequest) {
    Map<String, String> requestParameters = new HashMap<>();
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(CUSTOMERS_URL + "?");
    addQueryParamIfNotNull(requestParameters, urlBuilder, LAST_NAME, lastName);
    addQueryParamIfNotNull(requestParameters, urlBuilder, FIRST_NAME, firstName);
    addQueryParamIfNotNull(requestParameters, urlBuilder, CUSTOMER_IDS, customerIds);
    addQueryParamIfNotNull(requestParameters, urlBuilder, CITY, city);
    addQueryParamIfNotNull(requestParameters, urlBuilder, COMPANY, company);
    addQueryParamIfNotNull(requestParameters, urlBuilder, PRODUCT, product);
    addQueryParamIfNotNull(requestParameters, urlBuilder, SEARCH, search);
    if (pageRequest != null) {
      urlBuilder.append(PAGE_NUMBER + "={" + PAGE_NUMBER + "}&");
      urlBuilder.append(PAGE_SIZE + "={" + PAGE_SIZE + "}&");
      requestParameters.put(PAGE_NUMBER, String.valueOf(pageRequest.getPageNumber()));
      requestParameters.put(PAGE_SIZE, String.valueOf(pageRequest.getPageSize()));
    }
    final String builderContent = urlBuilder.toString();
    final String url = builderContent.substring(0, (builderContent.length() - 1));
    return testRestTemplate.getForEntity(url, ListCustomersResponseDTO.class, requestParameters);
  }

  public ResponseEntity<ListCustomersResponseDTO> getAllCustomers(Pageable pageRequest) {
    Map<String, String> requestParameters = new HashMap<>();
    StringBuilder urlBuilder = new StringBuilder();
    urlBuilder.append(ALL_CUSTOMERS_URL + "?");
    if (pageRequest != null) {
      urlBuilder.append(PAGE_NUMBER + "={" + PAGE_NUMBER + "}&");
      urlBuilder.append(PAGE_SIZE + "={" + PAGE_SIZE + "}&");
      requestParameters.put(PAGE_NUMBER, String.valueOf(pageRequest.getPageNumber()));
      requestParameters.put(PAGE_SIZE, String.valueOf(pageRequest.getPageSize()));
    }
    final String builderContent = urlBuilder.toString();
    final String url = builderContent.substring(0, (builderContent.length() - 1));
    return testRestTemplate.getForEntity(url, ListCustomersResponseDTO.class, requestParameters);
  }

  public void  deleteCustomer(Long customerId) {
    testRestTemplate.delete(CUSTOMER_URL, customerId);
  }

  public void deleteCustomer(Long customerId, HttpStatus expectedStatus) {
    var response = testRestTemplate.exchange(CUSTOMER_URL, HttpMethod.DELETE, null, Object.class, customerId);
    assertThat(response.getStatusCode(), is(expectedStatus));
  }

  private void addQueryParamIfNotNull(Map<String, String> requestParameters, StringBuilder urlBuilder, String paramName, List<String> paramValues) {
    if (!CollectionUtils.isEmpty(paramValues)) {
      urlBuilder.append(paramName).append("={").append(paramName).append("}&");
      requestParameters.put(paramName, String.join(",", paramValues));
    }
  }

  private void addQueryParamIfNotNull(Map<String, String> requestParameters, StringBuilder urlBuilder, String paramName, String paramValue) {
    if (paramValue != null) {
      urlBuilder.append(paramName).append("={").append(paramName).append("}&");
      requestParameters.put(paramName, paramValue);
    }
  }
}
