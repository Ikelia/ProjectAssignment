/**
 * A savings account that earns interest but has a minimum balance rule.
 * Demonstrates INHERITANCE — extends Account.
 * Demonstrates POLYMORPHISM — overrides withdraw() with its own business rule.
 */
public class SavingsAccount extends Account {

    private static final double MINIMUM_BALANCE = 100.0;
    private final double interestRate; // annual rate, e.g. 0.03 = 3 %

    public SavingsAccount(String accountNumber, double initialBalance,
                          Customer owner, double interestRate) {
        super(accountNumber, initialBalance, owner);
        this.interestRate = interestRate;
    }

    /** Savings accounts require a minimum balance after every withdrawal. */
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        if (getBalance() - amount < MINIMUM_BALANCE) {
            System.out.printf("  DENIED: Withdrawal of $%.2f - minimum balance of $%.2f required.%n",
                    amount, MINIMUM_BALANCE);
            return;
        }
        deductBalance(amount);
        System.out.printf("  Withdrew $%.2f -> balance: $%.2f%n", amount, getBalance());
    }

    /** Apply annual interest to the current balance. */
    public void applyInterest() {
        double interest = getBalance() * interestRate;
        deposit(interest);
        System.out.printf("  Interest applied (%.0f%%) -> +$%.2f%n", interestRate * 100, interest);
    }

    @Override
    public String getAccountType() { return "Savings Account"; }
}
