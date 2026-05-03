package model;

import exceptions.AccountNotFoundException;
import exceptions.CustomerNotFoundException;
import exceptions.DuplicateAccountException;
import exceptions.DuplicateCustomerException;

import java.util.*;

/**
 * Represents the bank — manages customers and their accounts.
 *
 * Collections used:
 *   Map<String, Customer>        customerRegistry    — key-value, O(1) lookup by ID
 *   Map<String, Account>         accountRegistry     — key-value, O(1) lookup by number
 *   Map<String, List<Account>>   accountsByCustomer  — one-to-many relationship
 */
public class Bank {

    private final String bankName;

    private final Map<String, Customer>       customerRegistry   = new LinkedHashMap<>();
    private final Map<String, Account>        accountRegistry    = new LinkedHashMap<>();
    private final Map<String, List<Account>>  accountsByCustomer = new HashMap<>();

    public Bank(String bankName) { this.bankName = bankName; }

    public String getBankName() { return bankName; }

    // ── Customer management ───────────────────────────────────────────────────

    public void addCustomer(Customer customer) {
        if (customerRegistry.containsKey(customer.getId())) {
            throw new DuplicateCustomerException(customer.getId());
        }
        customerRegistry.put(customer.getId(), customer);
        accountsByCustomer.put(customer.getId(), new ArrayList<>());
        System.out.println("  Customer registered: " + customer.getName() + " [" + customer.getId() + "]");
    }

    public Optional<Customer> findCustomer(String id) {
        return Optional.ofNullable(customerRegistry.get(id));
    }

    public void removeCustomer(String customerId) {
        if (!customerRegistry.containsKey(customerId)) throw new CustomerNotFoundException(customerId);
        List<Account> owned = accountsByCustomer.getOrDefault(customerId, List.of());
        owned.forEach(a -> accountRegistry.remove(a.getAccountNumber()));
        accountsByCustomer.remove(customerId);
        customerRegistry.remove(customerId);
        System.out.println("  Customer removed: " + customerId);
    }

    // ── Account management ────────────────────────────────────────────────────

    public void openAccount(Account account) {
        if (accountRegistry.containsKey(account.getAccountNumber())) {
            throw new DuplicateAccountException(account.getAccountNumber());
        }
        accountRegistry.put(account.getAccountNumber(), account);
        accountsByCustomer
                .computeIfAbsent(account.getOwner().getId(), k -> new ArrayList<>())
                .add(account);
        account.getOwner().registerAccountType(account.getAccountType());
        System.out.printf("  Account opened: %s (%s) for %s%n",
                account.getAccountNumber(), account.getAccountType(), account.getOwner().getName());
    }

    public Optional<Account> findAccount(String accountNumber) {
        return Optional.ofNullable(accountRegistry.get(accountNumber));
    }

    public void removeAccount(String accountNumber) {
        Account account = accountRegistry.get(accountNumber);
        if (account == null) throw new AccountNotFoundException(accountNumber);
        accountRegistry.remove(accountNumber);
        List<Account> owned = accountsByCustomer.get(account.getOwner().getId());
        if (owned != null) {
            owned.remove(account);
            boolean stillHasType = owned.stream()
                    .anyMatch(a -> a.getAccountType().equals(account.getAccountType()));
            if (!stillHasType) account.getOwner().unregisterAccountType(account.getAccountType());
        }
        System.out.println("  Account closed: " + accountNumber);
    }

    public List<Account> getAccountsForCustomer(String customerId) {
        return Collections.unmodifiableList(
                accountsByCustomer.getOrDefault(customerId, List.of()));
    }

    public Collection<Customer> getAllCustomers() {
        return Collections.unmodifiableCollection(customerRegistry.values());
    }

    public void printSummary() {
        System.out.println("\n==========================================");
        System.out.println("  " + bankName + " - Account Summary");
        System.out.println("==========================================");
        if (accountRegistry.isEmpty()) {
            System.out.println("  No accounts have been opened yet.");
            return;
        }
        for (Map.Entry<String, List<Account>> entry : accountsByCustomer.entrySet()) {
            entry.getValue().forEach(Account::printStatement);
        }
    }
}
