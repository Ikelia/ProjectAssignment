package model;

import exceptions.InvalidAmountException;
import exceptions.OverdraftLimitExceededException;

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
        System.out.printf("  Withdrew $%.2f -> new balance: $%.2f%n", amount, getBalance());
    }

    @Override public String getAccountType() { return "Checking Account"; }
    @Override public String getExtraParam()  { return String.valueOf(overdraftLimit); }
}
