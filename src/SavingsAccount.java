import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;

/**
 * A savings account that earns interest but enforces a minimum balance.
 * Demonstrates INHERITANCE — extends Account.
 * Demonstrates POLYMORPHISM — overrides withdraw() with its own business rule.
 * Throws InsufficientFundsException (custom unchecked) when minimum balance would be breached.
 */
public class SavingsAccount extends Account {

    private static final double MINIMUM_BALANCE = 100.0;
    private final double interestRate; // e.g. 0.03 = 3%

    public SavingsAccount(String accountNumber, double initialBalance,
                          Customer owner, double interestRate) {
        super(accountNumber, initialBalance, owner);
        this.interestRate = interestRate;
    }

    /**
     * Withdraws from savings account.
     * @throws InvalidAmountException      if amount is zero or negative
     * @throws InsufficientFundsException  if withdrawal would drop balance below minimum
     */
    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        if (getBalance() - amount < MINIMUM_BALANCE) {
            throw new InsufficientFundsException(amount, getBalance(), MINIMUM_BALANCE);
        }
        deductBalance(amount);
        System.out.printf("  Withdrew $%.2f -> new balance: $%.2f%n", amount, getBalance());
    }

    /** Applies annual interest to the current balance. */
    public void applyInterest() {
        double interest = getBalance() * interestRate;
        deposit(interest);
        System.out.printf("  Interest applied (%.0f%%) -> +$%.2f%n", interestRate * 100, interest);
    }

    @Override
    public String getAccountType() { return "Savings Account"; }
}
