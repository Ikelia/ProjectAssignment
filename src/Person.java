/**
 * Abstract base class representing any person in the system.
 * Demonstrates ABSTRACTION — defines common structure without full implementation.
 * Demonstrates ENCAPSULATION — fields are private with controlled access.
 */
public abstract class Person {

    private final String name;
    private final String id;

    public Person(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() { return name; }
    public String getId()   { return id; }

    /**
     * Every person must be able to introduce themselves.
     * Subclasses provide their own implementation (POLYMORPHISM).
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("%s [%s] - %s", name, id, getRole());
    }
}
