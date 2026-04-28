import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the bank itself — manages customers and their accounts.
 * Demonstrates ENCAPSULATION — internal lists are private.
 * Demonstrates ABSTRACTION — callers use high-level methods without knowing internals.
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
    public void addCustomer(Customer customer) {
        customers.add(customer);
        System.out.println("  Customer registered: " + customer.getName());
    }

    public Optional<Customer> findCustomer(String id) {
        return customers.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    // ── Account management ────────────────────────────────────────────────────
    public void openAccount(Account account) {
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

    /** Get all accounts belonging to a specific customer. */
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
        // POLYMORPHISM in action: printStatement() behaves differently per account type
        accounts.forEach(Account::printStatement);
    }
}
