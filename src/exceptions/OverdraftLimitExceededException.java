package exceptions;

/**
 * Thrown when a checking account withdrawal would exceed the overdraft limit.
 */
public class OverdraftLimitExceededException extends BankException {
    public OverdraftLimitExceededException(double requested, double balance, double overdraftLimit) {
        super(String.format(
            "Overdraft limit exceeded. Requested: $%.2f | Balance: $%.2f | Overdraft limit: $%.2f",
            requested, balance, overdraftLimit
        ));
    }
}
