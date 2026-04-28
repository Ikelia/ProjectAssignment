import exceptions.InvalidEmailException;

/**
 * A bank customer — a concrete Person.
 * Demonstrates INHERITANCE — extends Person and inherits name/id behaviour.
 * Throws InvalidEmailException (custom unchecked) if email is malformed.
 */
public class Customer extends Person {

    private String email;

    public Customer(String name, String id, String email) {
        super(name, id);
        setEmail(email); // validated through setter
    }

    public String getEmail() { return email; }

    /**
     * Validates email before storing it.
     * Throws InvalidEmailException if format is wrong.
     */
    public void setEmail(String email) {
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new InvalidEmailException(email);
        }
        this.email = email;
    }

    @Override
    public String getRole() { return "Customer"; }

    @Override
    public String toString() {
        return super.toString() + " | " + email;
    }
}
