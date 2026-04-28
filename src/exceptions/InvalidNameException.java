package exceptions;

/**
 * Thrown when a name contains digits or invalid characters.
 */
public class InvalidNameException extends BankException {
    public InvalidNameException(String name) {
        super("Invalid name: '" + name + "'. A name must contain letters only, no numbers.");
    }
}
