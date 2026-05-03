package com.bank.model;

import com.bank.exceptions.InvalidEmailException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A bank customer — a concrete Person.
 * Demonstrates INHERITANCE.
 *
 * Collection: Set<String> accountTypes
 *   Why Set? A customer holds unique account types — Set rejects duplicates.
 */
public class Customer extends Person {

    private String email;

    /*
     * COLLECTION — HashSet<String>
     * Relationship: customer holds a unique set of account types.
     * Set chosen because each account type can only appear once per customer.
     */
    private final Set<String> accountTypes = new HashSet<>();

    public Customer(String name, String id, String email) {
        super(name, id);
        setEmail(email);
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new InvalidEmailException(email);
        }
        this.email = email;
    }

    public boolean registerAccountType(String accountType)   { return accountTypes.add(accountType); }
    public boolean hasAccountType(String accountType)        { return accountTypes.contains(accountType); }
    public void    unregisterAccountType(String accountType) { accountTypes.remove(accountType); }

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
