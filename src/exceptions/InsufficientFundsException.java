package exceptions;

/**
 * Thrown when a savings account withdrawal would drop the balance
 * below the required minimum balance.
 */
public class InsufficientFundsException extends BankException {
    public InsufficientFundsException(double requested, double available, double minimum) {
        super(String.format(
            "Insufficient funds. Requested: $%.2f | Available: $%.2f | Minimum balance required: $%.2f",
            requested, available, minimum
        ));
    }
}
