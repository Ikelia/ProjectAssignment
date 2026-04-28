import exceptions.DuplicateAccountException;
import exceptions.DuplicateCustomerException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the bank — manages customers and accounts.
 * Demonstrates ENCAPSULATION — internal lists are private.
 * Demonstrates ABSTRACTION — callers use high-level methods without knowing internals.
 * Throws DuplicateCustomerException / DuplicateAccountException (custom unchecked)
 * to enforce uniqueness rules.
 */
public class Bank {

    private final String bankName;
    private final List<Customer> customers = new ArrayList<>();
    private final List<Account> accounts   = new ArrayList<>();

    public Bank(String bankName) {
        this.bankName = bankName;
    }

    public String getBankName() { return bankName; }

    // ── Customer management ───────────────────────────────────────────────────

    /**
     * Registers a new customer.
     * @throws DuplicateCustomerException if the customer ID is already in use
     */
    public void addCustomer(Customer customer) {
        if (findCustomer(customer.getId()).isPresent()) {
            throw new DuplicateCustomerException(customer.getId());
        }
        customers.add(customer);
        System.out.println("  Customer registered: " + customer.getName() + " [" + customer.getId() + "]");
    }

    public Optional<Customer> findCustomer(String id) {
        return customers.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    // ── Account management ────────────────────────────────────────────────────

    /**
     * Opens a new account.
     * @throws DuplicateAccountException if the account number is already in use
     */
    public void openAccount(Account account) {
        if (findAccount(account.getAccountNumber()).isPresent()) {
            throw new DuplicateAccountException(account.getAccountNumber());
        }
        accounts.add(account);
        System.out.printf("  Account opened: %s (%s) for %s%n",
                account.getAccountNumber(),
                account.getAccountType(),
                account.getOwner().getName());
    }

    public Optional<Account> findAccount(String accountNumber) {
        return accounts.stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst();
    }

    /** Returns all accounts belonging to a specific customer. */
    public List<Account> getAccountsForCustomer(String customerId) {
        return accounts.stream()
                .filter(a -> a.getOwner().getId().equals(customerId))
                .toList();
    }

    // ── Summary ───────────────────────────────────────────────────────────────
    public void printSummary() {
        System.out.println("\n==========================================");
        System.out.println("  " + bankName + " - Account Summary");
        System.out.println("==========================================");
        if (accounts.isEmpty()) {
            System.out.println("  No accounts have been opened yet.");
        } else {
            accounts.forEach(Account::printStatement);
        }
    }
}
