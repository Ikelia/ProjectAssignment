package fileio;

import model.Account;
import model.Bank;
import model.Customer;
import model.Transaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Writes all bank data to plain text files using BufferedWriter.
 *
 * File I/O approach:
 *   BufferedWriter wraps FileWriter for efficient buffered output.
 *   try-with-resources guarantees the writer is closed even on error.
 *   Each file is fully overwritten on every save.
 *
 * Files written:
 *   data/customers.txt    — format: id|name|email
 *   data/accounts.txt     — format: accountNumber|type|customerId|balance|extraParam
 *   data/transactions.txt — format: accountNumber|transactionType|amount|description
 */
public class BankDataWriter {

    private static final String DATA_DIR          = "data";
    private static final String CUSTOMERS_FILE    = DATA_DIR + "/customers.txt";
    private static final String ACCOUNTS_FILE     = DATA_DIR + "/accounts.txt";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.txt";

    /** Saves all bank data to the three data files. */
    public static void saveAll(Bank bank) {
        ensureDataDirectory();
        saveCustomers(bank.getAllCustomers());
        saveAccounts(bank);
        saveTransactions(bank);
    }

    // ── Write customers ───────────────────────────────────────────────────────

    private static void saveCustomers(Collection<Customer> customers) {
        // WRITE using BufferedWriter — efficient line-by-line output
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer c : customers) {
                writer.write(c.getId() + "|" + c.getName() + "|" + c.getEmail());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("  [FILE ERROR] Could not save customers: " + e.getMessage());
        }
    }

    // ── Write accounts ────────────────────────────────────────────────────────

    private static void saveAccounts(Bank bank) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (Customer c : bank.getAllCustomers()) {
                List<Account> accounts = bank.getAccountsForCustomer(c.getId());
                for (Account a : accounts) {
                    // extraParam = interestRate (Savings) or overdraftLimit (Checking)
                    writer.write(
                        a.getAccountNumber() + "|" +
                        a.getAccountType()   + "|" +
                        c.getId()            + "|" +
                        a.getBalance()       + "|" +
                        a.getExtraParam()
                    );
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("  [FILE ERROR] Could not save accounts: " + e.getMessage());
        }
    }

    // ── Write transactions ────────────────────────────────────────────────────

    private static void saveTransactions(Bank bank) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (Customer c : bank.getAllCustomers()) {
                for (Account a : bank.getAccountsForCustomer(c.getId())) {
                    for (Transaction t : a.getHistory()) {
                        writer.write(
                            a.getAccountNumber() + "|" +
                            t.getType()          + "|" +
                            t.getAmount()        + "|" +
                            t.getDescription()
                        );
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("  [FILE ERROR] Could not save transactions: " + e.getMessage());
        }
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private static void ensureDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }
}
