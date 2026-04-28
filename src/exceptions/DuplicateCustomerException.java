package exceptions;

/**
 * Thrown when attempting to register a customer with an ID that already exists.
 */
public class DuplicateCustomerException extends BankException {
    public DuplicateCustomerException(String customerId) {
        super("A customer with ID '" + customerId + "' is already registered.");
    }
}
