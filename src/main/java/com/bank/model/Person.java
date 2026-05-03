package com.bank.model;

import com.bank.exceptions.InvalidNameException;

/**
 * Abstract base class representing any person in the system.
 * Demonstrates ABSTRACTION and ENCAPSULATION.
 * Throws InvalidNameException if the name contains digits or is blank.
 */
public abstract class Person {

    private final String name;
    private final String id;

    public Person(String name, String id) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidNameException(name == null ? "" : name);
        }
        if (name.matches(".*\\d.*")) {
            throw new InvalidNameException(name);
        }
        this.name = name.trim();
        this.id   = id;
    }

    public String getName() { return name; }
    public String getId()   { return id; }

    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("%s [%s] - %s", name, id, getRole());
    }
}
