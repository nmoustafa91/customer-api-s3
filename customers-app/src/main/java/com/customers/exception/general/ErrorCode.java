package com.customers.exception.general;

import lombok.Getter;

/**
 * Application domain errors.
 */
@Getter
public enum ErrorCode {


    /**
     * Customer API error codes (14xx)
     */
    CUSTOMER_NOT_FOUND("CUSTOMERS_ERR_1401_CUSTOMER_NOT_FOUND", "Customer with the given customer id ({}) is not found."),
    CUSTOMER_EMAIL_ALREADY_EXISTS("CUSTOMERS_ERR_1402_CUSTOMER_EMAIL_ALREADY_EXISTS", "Customer with this email or username ({}) already exists."),
    TASK_NOT_FOUND("CUSTOMERS_ERR_1403_TASK_NOT_FOUND", "Task with the given task id ({}) is not found."),
    TASK_ALREADY_EXISTS("CUSTOMERS_ERR_1404_TASK_ALREADY_EXISTS", "Task with this id ({}) already exists and assigned to a customer."),
    
    ;

    ErrorCode(final String errorCode, final String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    private String errorCode;
    private String message;
}
