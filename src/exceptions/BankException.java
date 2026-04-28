package exceptions;

/**
 * Base class for all custom bank exceptions.
 * Extends RuntimeException — unchecked, so callers are not forced to declare it.
 *
 * Exception hierarchy:
 *   BankException
 *     ├── DuplicateCustomerException
 *     ├── DuplicateAccountException
 *     ├── CustomerNotFoundException
 *     ├── AccountNotFoundException
 *     ├── InvalidAmountException
 *     ├── InvalidEmailException
 *     ├── InsufficientFundsException
 *     └── OverdraftLimitExceededException
 */
public class BankException extends RuntimeException {
    public BankException(String message) {
        super(message);
    }
}
