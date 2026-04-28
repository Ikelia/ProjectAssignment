import exceptions.*;

import java.util.List;
import java.util.Scanner;

/**
 * Interactive ATM / Bank Account System — Main entry point.
 *
 * Demonstrates exception handling:
 *   - try-catch with multiple catch blocks
 *   - Custom exceptions caught and shown as friendly messages
 *   - Program never crashes — all errors are handled gracefully
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Bank bank = new Bank("OOP National Bank");

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   Welcome to " + bank.getBankName());
        System.out.println("==========================================");

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ", 1, 4);
            switch (choice) {
                case 1 -> registerAndOpenAccount();
                case 2 -> performTransaction();
                case 3 -> bank.printSummary();
                case 4 -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
            }
        }
        scanner.close();
    }

    // ── Menu ──────────────────────────────────────────────────────────────────

    private static void printMainMenu() {
        System.out.println("\n------------------------------------------");
        System.out.println("  MAIN MENU");
        System.out.println("------------------------------------------");
        System.out.println("  1. Register and open an account");
        System.out.println("  2. Deposit / Withdraw");
        System.out.println("  3. View all accounts (bank summary)");
        System.out.println("  4. Exit");
        System.out.println("------------------------------------------");
    }

    // ── Option 1: Register customer + open account ────────────────────────────

    private static void registerAndOpenAccount() {
        System.out.println("\n--- Register & Open Account ---");

        // ── Collect customer details ──────────────────────────────────────────
        String name  = readNonEmpty("Full name: ");
        String id    = readNonEmpty("Customer ID (e.g. C001): ");
        String email = readNonEmpty("Email address: ");

        // ── Try to create and register the customer ───────────────────────────
        Customer customer;
        try {
            customer = new Customer(name, id, email);  // may throw InvalidNameException or InvalidEmailException
            bank.addCustomer(customer);                 // may throw DuplicateCustomerException
        } catch (InvalidNameException e) {
            System.out.println("  REGISTRATION FAILED - " + e.getMessage());
            return;
        } catch (InvalidEmailException e) {
            System.out.println("  REGISTRATION FAILED - " + e.getMessage());
            return;
        } catch (DuplicateCustomerException e) {
            System.out.println("  REGISTRATION FAILED - " + e.getMessage());
            return;
        }

        // ── Open first account immediately ────────────────────────────────────
        System.out.println("\n  Now let's open your account.");
        openAccountFor(customer);

        // ── Offer a second account ────────────────────────────────────────────
        System.out.print("\n  Would you like to open another account? (yes/no): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            openAccountFor(customer);
        }
    }

    /** Opens one account for an already-registered customer. */
    private static void openAccountFor(Customer owner) {
        System.out.println("\n  Account type:");
        System.out.println("    1. Savings Account");
        System.out.println("    2. Checking Account");
        int type = readInt("  Choose (1 or 2): ", 1, 2);

        String accountNumber = readNonEmpty("  Account number (e.g. SA-1001): ");
        double initialBalance = readDouble("  Initial deposit amount ($): ", 0.01, Double.MAX_VALUE);

        try {
            if (type == 1) {
                double rate = readPercent("  Annual interest rate (e.g. 0.03 or 3%): ", 0.0, 1.0);
                bank.openAccount(new SavingsAccount(accountNumber, initialBalance, owner, rate));
            } else {
                double overdraft = readDouble("  Overdraft limit ($): ", 0.0, Double.MAX_VALUE);
                bank.openAccount(new CheckingAccount(accountNumber, initialBalance, owner, overdraft));
            }
        } catch (DuplicateAccountException e) {
            // Specific: account number already taken
            System.out.println("  ACCOUNT CREATION FAILED - " + e.getMessage());
        } catch (BankException e) {
            // Fallback for any other bank-related issue
            System.out.println("  ACCOUNT CREATION FAILED - " + e.getMessage());
        }
    }

    // ── Option 2: Deposit or Withdraw ─────────────────────────────────────────

    private static void performTransaction() {
        System.out.println("\n--- Deposit / Withdraw ---");

        String customerId = readNonEmpty("Your customer ID: ");

        // Look up customer — show friendly message if not found, never crash
        Customer customer;
        try {
            customer = bank.findCustomer(customerId)
                    .orElseThrow(() -> new CustomerNotFoundException(customerId));
        } catch (CustomerNotFoundException e) {
            System.out.println("  ERROR - " + e.getMessage());
            return;
        }

        List<Account> myAccounts = bank.getAccountsForCustomer(customerId);
        if (myAccounts.isEmpty()) {
            System.out.println("  No accounts found for '" + customer.getName()
                    + "'. Please open an account first (menu option 1).");
            return;
        }

        System.out.println("  Accounts for " + customer.getName() + ":");
        for (int i = 0; i < myAccounts.size(); i++) {
            Account a = myAccounts.get(i);
            System.out.printf("    %d. [%s] %-18s  Balance: $%.2f%n",
                    i + 1, a.getAccountNumber(), a.getAccountType(), a.getBalance());
        }

        int pick   = readInt("  Select account: ", 1, myAccounts.size());
        Account account = myAccounts.get(pick - 1);

        System.out.println("  Transaction type:");
        System.out.println("    1. Deposit");
        System.out.println("    2. Withdraw");
        int type   = readInt("  Choose (1 or 2): ", 1, 2);
        double amount = readDouble("  Amount ($): ", 0.01, Double.MAX_VALUE);

        try {
            if (type == 1) {
                account.deposit(amount);
            } else {
                account.withdraw(amount);
            }
        } catch (InsufficientFundsException e) {
            // Savings account minimum balance rule violated
            System.out.println("  TRANSACTION DENIED - " + e.getMessage());
        } catch (OverdraftLimitExceededException e) {
            // Checking account overdraft limit exceeded
            System.out.println("  TRANSACTION DENIED - " + e.getMessage());
        } catch (InvalidAmountException e) {
            // Amount was zero or negative (shouldn't reach here due to readDouble, but defensive)
            System.out.println("  TRANSACTION FAILED - " + e.getMessage());
        } catch (BankException e) {
            // Catch-all for any other bank exception
            System.out.println("  TRANSACTION FAILED - " + e.getMessage());
        }
    }

    // ── Input helpers ─────────────────────────────────────────────────────────

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("  ERROR: Input cannot be empty.");
        }
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
                System.out.println("  ERROR: Enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                // Built-in unchecked exception — non-numeric input
                System.out.println("  ERROR: '" + input + "' is not a valid number.");
            }
        }
    }

    private static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value >= min && value <= max) return value;
                System.out.println("  ERROR: Value must be greater than $" + min + ".");
            } catch (NumberFormatException e) {
                // Built-in unchecked exception — non-numeric input
                System.out.println("  ERROR: '" + input + "' is not a valid number.");
            }
        }
    }

    /** Accepts 0.04, 4, or 4% — always returns a decimal fraction. */
    private static double readPercent(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            String cleaned = input.endsWith("%") ? input.substring(0, input.length() - 1).trim() : input;
            try {
                double value = Double.parseDouble(cleaned);
                if (value > 1.0) value = value / 100.0;
                if (value >= min && value <= max) return value;
                System.out.println("  ERROR: Rate must be between 0% and 100%.");
            } catch (NumberFormatException e) {
                // Built-in unchecked exception — non-numeric input
                System.out.println("  ERROR: '" + input + "' is not valid. Use 0.04, 4, or 4%.");
            }
        }
    }
}
