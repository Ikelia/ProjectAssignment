package com.bank.exceptions;

/** Thrown when a deposit or withdrawal amount is zero or negative. */
public class InvalidAmountException extends BankException {
    public InvalidAmountException(double amount) {
        super("Transaction amount must be positive. Received: $" + String.format("%.2f", amount));
    }
}
