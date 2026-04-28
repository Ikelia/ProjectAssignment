/**
 * A checking account with overdraft protection.
 * Demonstrates INHERITANCE — extends Account.
 * Demonstrates POLYMORPHISM — overrides withdraw() with different logic than SavingsAccount.
 */
public class CheckingAccount extends Account {

    private final double overdraftLimit; // how much you can go negative

    public CheckingAccount(String accountNumber, double initialBalance,
                           Customer owner, double overdraftLimit) {
        super(accountNumber, initialBalance, owner);
        this.overdraftLimit = overdraftLimit;
    }

    /** Checking accounts allow overdraft up to a limit. */
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        if (getBalance() - amount < -overdraftLimit) {
            System.out.printf("  DENIED: Withdrawal of $%.2f - overdraft limit of $%.2f exceeded.%n",
                    amount, overdraftLimit);
            return;
        }
        deductBalance(amount);
        System.out.printf("  Withdrew $%.2f -> balance: $%.2f%n", amount, getBalance());
    }

    @Override
    public String getAccountType() { return "Checking Account"; }
}
