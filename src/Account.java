import exceptions.InvalidAmountException;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract representation of a bank account.
 * Demonstrates ABSTRACTION — defines the contract every account must fulfil.
 * Demonstrates ENCAPSULATION — balance is private; modified only through methods.
 * Throws InvalidAmountException (custom unchecked) for bad deposit amounts.
 */
public abstract class Account {

    private final String accountNumber;
    private double balance;
    private final Customer owner;
    private final List<Transaction> history = new ArrayList<>();

    public Account(String accountNumber, double initialBalance, Customer owner) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.owner = owner;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getAccountNumber()      { return accountNumber; }
    public double getBalance()            { return balance; }
    public Customer getOwner()            { return owner; }
    public List<Transaction> getHistory() { return List.copyOf(history); }

    // ── Core operations ───────────────────────────────────────────────────────

    /**
     * Deposits money into the account.
     * @throws InvalidAmountException if amount is zero or negative
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        balance += amount;
        history.add(new Transaction(Transaction.Type.DEPOSIT, amount, "Deposit"));
        System.out.printf("  Deposited $%.2f -> new balance: $%.2f%n", amount, balance);
    }

    /**
     * Withdraw money. Each account type enforces its own rules (POLYMORPHISM).
     * Subclasses throw their own custom exceptions on rule violations.
     */
    public abstract void withdraw(double amount);

    /**
     * Returns a label describing the account type (POLYMORPHISM).
     */
    public abstract String getAccountType();

    // ── Shared helper used by subclasses ──────────────────────────────────────
    protected void deductBalance(double amount) {
        balance -= amount;
        history.add(new Transaction(Transaction.Type.WITHDRAWAL, amount, "Withdrawal"));
    }

    // ── Display ───────────────────────────────────────────────────────────────
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
