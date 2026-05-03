package com.bank.model;

import com.bank.exceptions.InsufficientFundsException;
import com.bank.exceptions.InvalidAmountException;

/**
 * A savings account that earns interest but enforces a minimum balance.
 * Demonstrates INHERITANCE and POLYMORPHISM.
 */
public class SavingsAccount extends Account {

    private static final double MINIMUM_BALANCE = 100.0;
    private final double interestRate;

    public SavingsAccount(String accountNumber, double initialBalance,
                          Customer owner, double interestRate) {
        super(accountNumber, initialBalance, owner);
        this.interestRate = interestRate;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) throw new InvalidAmountException(amount);
        if (getBalance() - amount < MINIMUM_BALANCE) {
            throw new InsufficientFundsException(amount, getBalance(), MINIMUM_BALANCE);
        }
        deductBalance(amount);
    }

    public void applyInterest() {
        double interest = getBalance() * interestRate;
        deposit(interest);
    }

    public double getInterestRate() { return interestRate; }

    @Override public String getAccountType() { return "Savings Account"; }
    @Override public String getExtraParam()  { return String.valueOf(interestRate); }
}
