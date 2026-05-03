package com.bank.fileio;

import com.bank.model.Account;
import com.bank.model.Bank;
import com.bank.model.Customer;
import com.bank.model.Transaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Writes all bank data to plain text files using BufferedWriter.
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

    private static void saveCustomers(Collection<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer c : customers) {
                writer.write(c.getId() + "|" + c.getName() + "|" + c.getEmail());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("[FILE ERROR] Could not save customers: " + e.getMessage());
        }
    }

    private static void saveAccounts(Bank bank) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (Customer c : bank.getAllCustomers()) {
                List<Account> accounts = bank.getAccountsForCustomer(c.getId());
                for (Account a : accounts) {
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
            System.out.println("[FILE ERROR] Could not save accounts: " + e.getMessage());
        }
    }

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
            System.out.println("[FILE ERROR] Could not save transactions: " + e.getMessage());
        }
    }

    private static void ensureDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }
}
