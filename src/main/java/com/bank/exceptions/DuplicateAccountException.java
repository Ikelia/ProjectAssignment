package com.bank.exceptions;

/** Thrown when attempting to open an account with a number that already exists. */
public class DuplicateAccountException extends BankException {
    public DuplicateAccountException(String accountNumber) {
        super("An account with number '" + accountNumber + "' already exists.");
    }
}
