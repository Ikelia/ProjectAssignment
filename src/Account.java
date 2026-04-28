import exceptions.InvalidAmountException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract representation of a bank account.
 * Demonstrates ABSTRACTION — defines the contract every account must fulfil.
 * Demonstrates ENCAPSULATION — balance is private; modified only through methods.
 *
 * ── Collection used and why ───────────────────────────────────────────────────
 *
 * LinkedList<Transaction>  history
 * Relationship : Account  ──(records many)──>  Transaction   (one-to-many)
 * Why LinkedList?
 *   - Transactions are always appended at the end (addLast) — O(1).
 *   - We never need random access by index; we only iterate from first to last.
 *   - LinkedList is ideal for an ordered sequence of events where
 *     insertion order matters and random access is not needed.
 *   - Contrast with ArrayList: ArrayList is better when random access is frequent.
 *     For a transaction log, we only ever add and iterate — LinkedList fits perfectly.
 */
public abstract class Account {

    private final String accountNumber;
    private double balance;
    private final Customer owner;

    /*
     * COLLECTION — LinkedList<Transaction>
     * Relationship: one account records many transactions in order.
     * LinkedList chosen because transactions are always appended (O(1) addLast)
     * and only ever iterated — never accessed by index.
     */
    private final LinkedList<Transaction> history = new LinkedList<>();

    public Account(String accountNumber, double initialBalance, Customer owner) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.owner = owner;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getAccountNumber()      { return accountNumber; }
    public double getBalance()            { return balance; }
    public Customer getOwner()            { return owner; }

    /** Returns an unmodifiable view of the transaction history (in order). */
    public List<Transaction> getHistory() {
        return Collections.unmodifiableList(history);
    }

    // ── Core operations ───────────────────────────────────────────────────────

    /**
     * Deposits money into the account.
     * ADD operation on LinkedList — appends a new Transaction to the history.
     * @throws InvalidAmountException if amount is zero or negative
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        balance += amount;
        // ADD to LinkedList
        history.addLast(new Transaction(Transaction.Type.DEPOSIT, amount, "Deposit"));
        System.out.printf("  Deposited $%.2f -> new balance: $%.2f%n", amount, balance);
    }

    /**
     * Withdraw money. Each account type enforces its own rules (POLYMORPHISM).
     */
    public abstract void withdraw(double amount);

    /** Returns a label describing the account type (POLYMORPHISM). */
    public abstract String getAccountType();

    // ── Shared helper used by subclasses ──────────────────────────────────────
    protected void deductBalance(double amount) {
        balance -= amount;
        // ADD to LinkedList
        history.addLast(new Transaction(Transaction.Type.WITHDRAWAL, amount, "Withdrawal"));
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
            // RETRIEVE — iterate LinkedList in insertion order
            history.forEach(t -> System.out.println("    " + t));
        }
        System.out.println("-----------------------------------------");
    }
}
