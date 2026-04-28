/**
 * A bank customer — a concrete Person.
 * Demonstrates INHERITANCE — extends Person and inherits name/id behaviour.
 */
public class Customer extends Person {

    private String email;

    public Customer(String name, String id, String email) {
        super(name, id);   // reuse Person constructor
        this.email = email;
    }

    public String getEmail()              { return email; }
    public void setEmail(String email)    { this.email = email; }

    @Override
    public String getRole() { return "Customer"; }

    @Override
    public String toString() {
        return super.toString() + " | " + email;
    }
}
