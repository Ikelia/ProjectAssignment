package com.bank.fileio;

import com.bank.model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads bank data from text files and restores system state using BufferedReader.
 *
 * File I/O approach:
 *   BufferedReader wraps FileReader for efficient buffered line-by-line reading.
 *   try-with-resources ensures the reader is always closed after use.
 *   Silently skips missing files (first run — no data yet).
 *
 * Load order (important — accounts reference customers, transactions reference accounts):
 *   1. customers.txt
 *   2. accounts.txt
 *   3. transactions.txt
 */
public class BankDataReader {

    private static final String DATA_DIR          = "data";
    private static final String CUSTOMERS_FILE    = DATA_DIR + "/customers.txt";
    private static final String ACCOUNTS_FILE     = DATA_DIR + "/accounts.txt";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.txt";

    /** Loads all saved data into the given Bank instance. */
    public static void loadAll(Bank bank) {
        loadCustomers(bank);
        loadAccounts(bank);
        loadTransactions(bank);
    }

    // ── Read customers ────────────────────────────────────────────────────────

    private static void loadCustomers(Bank bank) {
        File file = new File(CUSTOMERS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length != 3) continue;
                try {
                    bank.addCustomer(new Customer(parts[1].trim(), parts[0].trim(), parts[2].trim()));
                } catch (Exception e) {
                    System.out.println("[FILE WARNING] Could not restore customer on line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("[FILE ERROR] Could not read customers file: " + e.getMessage());
        }
    }

    // ── Read accounts ─────────────────────────────────────────────────────────

    private static void loadAccounts(Bank bank) {
        File file = new File(ACCOUNTS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length != 5) continue;
                try {
                    String accountNumber = parts[0].trim();
                    String type          = parts[1].trim();
                    String customerId    = parts[2].trim();
                    double balance       = Double.parseDouble(parts[3].trim());
                    double extraParam    = Double.parseDouble(parts[4].trim());

                    Customer owner = bank.findCustomer(customerId).orElse(null);
                    if (owner == null) continue;

                    Account account;
                    if (type.equals("Savings Account")) {
                        account = new SavingsAccount(accountNumber, balance, owner, extraParam);
                    } else if (type.equals("Checking Account")) {
                        account = new CheckingAccount(accountNumber, balance, owner, extraParam);
                    } else {
                        continue;
                    }
                    bank.openAccount(account);
                } catch (Exception e) {
                    System.out.println("[FILE WARNING] Could not restore account on line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("[FILE ERROR] Could not read accounts file: " + e.getMessage());
        }
    }

    // ── Read transactions ─────────────────────────────────────────────────────

    private static void loadTransactions(Bank bank) {
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length != 4) continue;
                try {
                    String accountNumber = parts[0].trim();
                    String typeStr       = parts[1].trim();
                    double amount        = Double.parseDouble(parts[2].trim());
                    String description   = parts[3].trim();

                    Account account = bank.findAccount(accountNumber).orElse(null);
                    if (account == null) continue;

                    Transaction.Type type = Transaction.Type.valueOf(typeStr);
                    account.restoreTransaction(new Transaction(type, amount, description));
                } catch (Exception e) {
                    System.out.println("[FILE WARNING] Could not restore transaction on line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("[FILE ERROR] Could not read transactions file: " + e.getMessage());
        }
    }
}
