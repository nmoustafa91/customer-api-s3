package com.customers.domain;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class CustomerFilter {

  private String lastName;
  private String firstName;
  private String city;
  private String companyName;
  private String productNumber;
  private String search;
  private List<Long> customersIds;
}
