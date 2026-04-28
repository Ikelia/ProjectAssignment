/**
 * Represents a single financial transaction (deposit or withdrawal).
 * Demonstrates ENCAPSULATION — all fields are private, accessed via getters.
 */
public class Transaction {

    public enum Type { DEPOSIT, WITHDRAWAL }

    private final Type type;
    private final double amount;
    private final String description;

    public Transaction(Type type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    public Type getType()          { return type; }
    public double getAmount()      { return amount; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format("[%s] %s: $%.2f", type, description, amount);
    }
}
