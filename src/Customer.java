import exceptions.InvalidEmailException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A bank customer — a concrete Person.
 * Demonstrates INHERITANCE — extends Person and inherits name/id behaviour.
 *
 * ── Collection used and why ───────────────────────────────────────────────────
 *
 * Set<String>  accountTypes
 * Relationship : Customer  ──(holds unique types of)──>  AccountType
 * Why Set?
 *   - A customer should not hold two accounts of the exact same type
 *     (e.g. two Savings Accounts). Set automatically rejects duplicates.
 *   - We only need to know whether a type exists — no ordering needed.
 *   - Set models a "unique membership" relationship perfectly.
 *   - Contrast with List: a List would allow duplicates, which is wrong here.
 */
public class Customer extends Person {

    private String email;

    /*
     * COLLECTION — HashSet<String>
     * Relationship: a customer holds a unique set of account types.
     * Set chosen because each account type can only appear once per customer.
     * HashSet gives O(1) add and contains checks.
     */
    private final Set<String> accountTypes = new HashSet<>();

    public Customer(String name, String id, String email) {
        super(name, id);
        setEmail(email);
    }

    public String getEmail() { return email; }

    /**
     * Validates email before storing it.
     * @throws InvalidEmailException if format is wrong
     */
    public void setEmail(String email) {
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new InvalidEmailException(email);
        }
        this.email = email;
    }

    /**
     * ADD operation on Set — registers an account type for this customer.
     * Returns false (and does nothing) if the type is already registered,
     * because Set rejects duplicates.
     */
    public boolean registerAccountType(String accountType) {
        return accountTypes.add(accountType); // Set.add returns false if already present
    }

    /**
     * RETRIEVE — check if customer already holds a given account type.
     */
    public boolean hasAccountType(String accountType) {
        return accountTypes.contains(accountType);
    }

    /**
     * REMOVE — unregister an account type when an account is closed.
     */
    public void unregisterAccountType(String accountType) {
        accountTypes.remove(accountType);
    }

    /** Returns an unmodifiable view of the account types this customer holds. */
    public Set<String> getAccountTypes() {
        return Collections.unmodifiableSet(accountTypes);
    }

    @Override
    public String getRole() { return "Customer"; }

    @Override
    public String toString() {
        return super.toString() + " | " + email;
    }
}
