package model;

import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;

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
        System.out.printf("  Withdrew $%.2f -> new balance: $%.2f%n", amount, getBalance());
    }

    public void applyInterest() {
        double interest = getBalance() * interestRate;
        deposit(interest);
        System.out.printf("  Interest applied (%.0f%%) -> +$%.2f%n", interestRate * 100, interest);
    }

    @Override public String getAccountType() { return "Savings Account"; }
    @Override public String getExtraParam()  { return String.valueOf(interestRate); }
}
