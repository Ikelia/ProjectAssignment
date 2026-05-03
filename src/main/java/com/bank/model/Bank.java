package com.bank.model;

import com.bank.exceptions.AccountNotFoundException;
import com.bank.exceptions.CustomerNotFoundException;
import com.bank.exceptions.DuplicateAccountException;
import com.bank.exceptions.DuplicateCustomerException;

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

    private final Map<String, Customer>      customerRegistry   = new LinkedHashMap<>();
    private final Map<String, Account>       accountRegistry    = new LinkedHashMap<>();
    private final Map<String, List<Account>> accountsByCustomer = new HashMap<>();

    public Bank(String bankName) { this.bankName = bankName; }

    // Convenience constructor used by MainApp
    public Bank() { this("National Bank"); }

    public String getBankName() { return bankName; }

    // ── Customer management ───────────────────────────────────────────────────

    public void addCustomer(Customer customer) {
        if (customerRegistry.containsKey(customer.getId())) {
            throw new DuplicateCustomerException(customer.getId());
        }
        customerRegistry.put(customer.getId(), customer);
        accountsByCustomer.put(customer.getId(), new ArrayList<>());
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
    }

    public List<Account> getAccountsForCustomer(String customerId) {
        return Collections.unmodifiableList(
                accountsByCustomer.getOrDefault(customerId, List.of()));
    }

    public Collection<Customer> getAllCustomers() {
        return Collections.unmodifiableCollection(customerRegistry.values());
    }

    /** Returns all accounts across all customers. */
    public Collection<Account> getAllAccounts() {
        return Collections.unmodifiableCollection(accountRegistry.values());
    }

    /** Sorts all customers by name (A→Z). */
    public List<Customer> getCustomersSortedByName() {
        List<Customer> sorted = new ArrayList<>(customerRegistry.values());
        sorted.sort(Comparator.comparing(Customer::getName));
        return sorted;
    }

    /** Sorts all accounts by balance descending. */
    public List<Account> getAccountsSortedByBalance() {
        List<Account> sorted = new ArrayList<>(accountRegistry.values());
        sorted.sort(Comparator.comparingDouble(Account::getBalance).reversed());
        return sorted;
    }
}
