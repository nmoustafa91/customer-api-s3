package com.customers.service;

public interface IonosS3Service {

  /**
   *
   * It is scheduled to run every two hours to measure the delta customers which either:
   *  updated or created directly in the last two hours (changes on customer entity itself).
   *  or the customers that were updated based on the contract assignment.
   */
  void uploadDeltaCustomersDataToS3Storage();
}
