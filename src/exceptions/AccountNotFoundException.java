package exceptions;

/**
 * Thrown when an account lookup fails (account number does not exist).
 */
public class AccountNotFoundException extends BankException {
    public AccountNotFoundException(String accountNumber) {
        super("No account found with number '" + accountNumber + "'.");
    }
}
