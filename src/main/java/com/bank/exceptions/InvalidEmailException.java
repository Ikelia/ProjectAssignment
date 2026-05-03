package com.bank.exceptions;

/** Thrown when a customer is created with a malformed email address. */
public class InvalidEmailException extends BankException {
    public InvalidEmailException(String email) {
        super("Invalid email address: '" + email + "'. Must contain '@' and '.'");
    }
}
