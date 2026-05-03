package com.bank.exceptions;

/** Thrown when a customer lookup fails (ID does not exist in the system). */
public class CustomerNotFoundException extends BankException {
    public CustomerNotFoundException(String customerId) {
        super("No customer found with ID '" + customerId + "'.");
    }
}
