package model;

import exceptions.InvalidAmountException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract representation of a bank account.
 * Demonstrates ABSTRACTION and ENCAPSULATION.
 *
 * Collection: LinkedList<Transaction> history
 *   Why LinkedList? Transactions are always appended (O(1) addLast) and only
 *   ever iterated — never accessed by index. LinkedList is ideal for this.
 */
public abstract class Account {

    private final String accountNumber;
    private double balance;
    private final Customer owner;

    /*
     * COLLECTION — LinkedList<Transaction>
     * Ordered sequence of events; always appended, never random-accessed.
     */
    private final LinkedList<Transaction> history = new LinkedList<>();

    public Account(String accountNumber, double initialBalance, Customer owner) {
        this.accountNumber = accountNumber;
        this.balance       = initialBalance;
        this.owner         = owner;
    }

    public String   getAccountNumber() { return accountNumber; }
    public double   getBalance()       { return balance; }
    public Customer getOwner()         { return owner; }

    public List<Transaction> getHistory() {
        return Collections.unmodifiableList(history);
    }

    /**
     * Deposits money. Throws InvalidAmountException if amount <= 0.
     */
    public void deposit(double amount) {
        if (amount <= 0) throw new InvalidAmountException(amount);
        balance += amount;
        history.addLast(new Transaction(Transaction.Type.DEPOSIT, amount, "Deposit"));
        System.out.printf("  Deposited $%.2f -> new balance: $%.2f%n", amount, balance);
    }

    public abstract void   withdraw(double amount);
    public abstract String getAccountType();

    /**
     * Returns the type-specific parameter for file persistence.
     * SavingsAccount  -> interest rate
     * CheckingAccount -> overdraft limit
     */
    public abstract String getExtraParam();

    /**
     * Restores a transaction into history WITHOUT changing the balance.
     * Used by BankDataReader when replaying saved transactions on startup.
     */
    public void restoreTransaction(Transaction transaction) {
        history.addLast(transaction);
    }

    protected void deductBalance(double amount) {
        balance -= amount;
        history.addLast(new Transaction(Transaction.Type.WITHDRAWAL, amount, "Withdrawal"));
    }

    public void printStatement() {
        System.out.println("-----------------------------------------");
        System.out.printf("  %s  |  Account: %s%n", getAccountType(), accountNumber);
        System.out.printf("  Owner  : %s%n", owner.getName());
        System.out.printf("  Balance: $%.2f%n", balance);
        System.out.println("  Transaction History:");
        if (history.isEmpty()) {
            System.out.println("    (no transactions yet)");
        } else {
            history.forEach(t -> System.out.println("    " + t));
        }
        System.out.println("-----------------------------------------");
    }
}
