package com.bank.model;

import com.bank.exceptions.InvalidAmountException;
import com.bank.exceptions.OverdraftLimitExceededException;

/**
 * A checking account with overdraft protection.
 * Demonstrates INHERITANCE and POLYMORPHISM.
 */
public class CheckingAccount extends Account {

    private final double overdraftLimit;

    public CheckingAccount(String accountNumber, double initialBalance,
                           Customer owner, double overdraftLimit) {
        super(accountNumber, initialBalance, owner);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) throw new InvalidAmountException(amount);
        if (getBalance() - amount < -overdraftLimit) {
            throw new OverdraftLimitExceededException(amount, getBalance(), overdraftLimit);
        }
        deductBalance(amount);
    }

    public double getOverdraftLimit() { return overdraftLimit; }

    @Override public String getAccountType() { return "Checking Account"; }
    @Override public String getExtraParam()  { return String.valueOf(overdraftLimit); }
}
