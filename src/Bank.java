import exceptions.DuplicateAccountException;
import exceptions.DuplicateCustomerException;
import exceptions.AccountNotFoundException;
import exceptions.CustomerNotFoundException;

import java.util.*;

/**
 * Represents the bank — manages customers and their accounts.
 *
 * ── Collections used and why ──────────────────────────────────────────────────
 *
 * 1. Map<String, Customer>  customerRegistry
 *    Relationship : Bank  ──(has)──>  Customer
 *    Why Map?     : A customer is always looked up by their unique ID.
 *                   Map gives O(1) lookup by key (ID) instead of scanning a List.
 *                   The ID is the natural key — this is a key-value relationship.
 *
 * 2. Map<String, List<Account>>  accountsByCustomer
 *    Relationship : Customer  ──(owns many)──>  Account   (one-to-many)
 *    Why Map<String, List>?
 *      - The outer Map keys on customer ID for fast lookup.
 *      - The inner List holds all accounts for that customer in insertion order.
 *      - This is the classic "one-to-many via Map" pattern:
 *        one customer ID maps to a list of their accounts.
 *
 * 3. Map<String, Account>  accountRegistry
 *    Relationship : Bank  ──(has)──>  Account
 *    Why Map?     : Accounts are looked up by account number (unique key).
 *                   O(1) lookup, and enforces uniqueness naturally.
 */
public class Bank {

    private final String bankName;

    /*
     * COLLECTION 1 — Map<String, Customer>
     * Key   : customer ID (unique)
     * Value : Customer object
     * Reason: Key-value relationship — we always look up customers by ID.
     */
    private final Map<String, Customer> customerRegistry = new LinkedHashMap<>();

    /*
     * COLLECTION 2 — Map<String, Account>
     * Key   : account number (unique)
     * Value : Account object
     * Reason: Key-value relationship — fast lookup of any account by its number.
     */
    private final Map<String, Account> accountRegistry = new LinkedHashMap<>();

    /*
     * COLLECTION 3 — Map<String, List<Account>>
     * Key   : customer ID
     * Value : List of accounts owned by that customer (one-to-many)
     * Reason: One customer owns many accounts. Map gives fast access per customer;
     *         List preserves the order accounts were opened.
     */
    private final Map<String, List<Account>> accountsByCustomer = new HashMap<>();

    public Bank(String bankName) {
        this.bankName = bankName;
    }

    public String getBankName() { return bankName; }

    // ── Customer management ───────────────────────────────────────────────────

    /**
     * Registers a new customer.
     * ADD operation on Map — puts customer into customerRegistry.
     * @throws DuplicateCustomerException if the ID is already in use
     */
    public void addCustomer(Customer customer) {
        if (customerRegistry.containsKey(customer.getId())) {
            throw new DuplicateCustomerException(customer.getId());
        }
        // ADD to Map
        customerRegistry.put(customer.getId(), customer);
        // Initialise their empty account list in the one-to-many map
        accountsByCustomer.put(customer.getId(), new ArrayList<>());
        System.out.println("  Customer registered: " + customer.getName() + " [" + customer.getId() + "]");
    }

    /**
     * RETRIEVE from Map — O(1) lookup by customer ID.
     */
    public Optional<Customer> findCustomer(String id) {
        return Optional.ofNullable(customerRegistry.get(id));
    }

    /**
     * REMOVE from Map — deregisters a customer and all their accounts.
     * @throws CustomerNotFoundException if the ID does not exist
     */
    public void removeCustomer(String customerId) {
        if (!customerRegistry.containsKey(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
        // Remove all accounts belonging to this customer from the account registry
        List<Account> owned = accountsByCustomer.getOrDefault(customerId, List.of());
        owned.forEach(a -> accountRegistry.remove(a.getAccountNumber()));

        // Remove from both maps
        accountsByCustomer.remove(customerId);
        customerRegistry.remove(customerId);
        System.out.println("  Customer removed: " + customerId);
    }

    // ── Account management ────────────────────────────────────────────────────

    /**
     * Opens a new account.
     * ADD operation — puts account into accountRegistry and appends to the
     * customer's List inside accountsByCustomer (one-to-many).
     * Also registers the account type in the customer's Set (unique types).
     * @throws DuplicateAccountException if the account number is already in use
     */
    public void openAccount(Account account) {
        if (accountRegistry.containsKey(account.getAccountNumber())) {
            throw new DuplicateAccountException(account.getAccountNumber());
        }
        // ADD to account registry Map
        accountRegistry.put(account.getAccountNumber(), account);

        // ADD to the customer's List in the one-to-many Map
        accountsByCustomer
                .computeIfAbsent(account.getOwner().getId(), k -> new ArrayList<>())
                .add(account);

        // ADD account type to the customer's Set (Set ignores if already present)
        account.getOwner().registerAccountType(account.getAccountType());

        System.out.printf("  Account opened: %s (%s) for %s%n",
                account.getAccountNumber(),
                account.getAccountType(),
                account.getOwner().getName());
    }

    /**
     * RETRIEVE from Map — O(1) lookup by account number.
     */
    public Optional<Account> findAccount(String accountNumber) {
        return Optional.ofNullable(accountRegistry.get(accountNumber));
    }

    /**
     * REMOVE an account from the bank.
     * Removes from accountRegistry and from the customer's List.
     * Also unregisters the account type from the customer's Set.
     * @throws AccountNotFoundException if the account number does not exist
     */
    public void removeAccount(String accountNumber) {
        Account account = accountRegistry.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        // REMOVE from account registry Map
        accountRegistry.remove(accountNumber);

        // REMOVE from the customer's List in the one-to-many Map
        List<Account> owned = accountsByCustomer.get(account.getOwner().getId());
        if (owned != null) {
            owned.remove(account);
            // Only unregister the type from the Set if no other account of same type remains
            boolean stillHasType = owned.stream()
                    .anyMatch(a -> a.getAccountType().equals(account.getAccountType()));
            if (!stillHasType) {
                account.getOwner().unregisterAccountType(account.getAccountType());
            }
        }
        System.out.println("  Account closed: " + accountNumber);
    }

    /**
     * RETRIEVE all accounts for a customer from the one-to-many Map.
     * Returns an unmodifiable view so callers cannot mutate the internal list.
     */
    public List<Account> getAccountsForCustomer(String customerId) {
        return Collections.unmodifiableList(
                accountsByCustomer.getOrDefault(customerId, List.of())
        );
    }

    /**
     * RETRIEVE all registered customers (values of the Map).
     */
    public Collection<Customer> getAllCustomers() {
        return Collections.unmodifiableCollection(customerRegistry.values());
    }

    // ── Summary ───────────────────────────────────────────────────────────────
    public void printSummary() {
        System.out.println("\n==========================================");
        System.out.println("  " + bankName + " - Account Summary");
        System.out.println("==========================================");
        if (accountRegistry.isEmpty()) {
            System.out.println("  No accounts have been opened yet.");
            return;
        }
        // Iterate over the one-to-many Map: each customer -> their list of accounts
        for (Map.Entry<String, List<Account>> entry : accountsByCustomer.entrySet()) {
            List<Account> accounts = entry.getValue();
            if (!accounts.isEmpty()) {
                accounts.forEach(Account::printStatement);
            }
        }
    }
}
