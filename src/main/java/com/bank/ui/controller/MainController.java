package com.bank.ui.controller;

import com.bank.exceptions.BankException;
import com.bank.fileio.BankDataReader;
import com.bank.fileio.BankDataWriter;
import com.bank.logging.BankLogger;
import com.bank.model.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * JavaFX Controller — bridges UI events to backend (Bank) operations.
 *
 * Responsibilities:
 *   - Capture input from UI fields
 *   - Call Bank / Account methods
 *   - Update TableViews and labels
 *   - Display success / error messages
 *   - Persist data via BankDataWriter after every mutation
 *
 * UI updates always happen on the JavaFX Application Thread.
 * Background work uses Platform.runLater() where needed.
 */
public class MainController implements Initializable {

    // ── Shared bank instance ──────────────────────────────────────────────────
    private final Bank bank = new Bank("National Bank");

    // ── Tab: Customers ────────────────────────────────────────────────────────
    @FXML private TextField tfCustName;
    @FXML private TextField tfCustId;
    @FXML private TextField tfCustEmail;
    @FXML private TextField tfSearchCustId;
    @FXML private Label     lblCustMessage;

    @FXML private TableView<Customer>              tblCustomers;
    @FXML private TableColumn<Customer, String>    colCustId;
    @FXML private TableColumn<Customer, String>    colCustName;
    @FXML private TableColumn<Customer, String>    colCustEmail;

    private final ObservableList<Customer> customerList = FXCollections.observableArrayList();

    // ── Tab: Accounts ─────────────────────────────────────────────────────────
    @FXML private TextField     tfAccNumber;
    @FXML private TextField     tfAccOwnerId;
    @FXML private ComboBox<String> cbAccType;
    @FXML private TextField     tfAccBalance;
    @FXML private TextField     tfAccExtra;
    @FXML private Label         lblAccExtraHint;
    @FXML private TextField     tfSearchAccId;
    @FXML private Label         lblAccMessage;

    @FXML private TableView<Account>            tblAccounts;
    @FXML private TableColumn<Account, String>  colAccNumber;
    @FXML private TableColumn<Account, String>  colAccType;
    @FXML private TableColumn<Account, String>  colAccOwner;
    @FXML private TableColumn<Account, String>  colAccBalance;
    @FXML private TableColumn<Account, String>  colAccExtra;

    private final ObservableList<Account> accountList = FXCollections.observableArrayList();

    // ── Tab: Transactions ─────────────────────────────────────────────────────
    @FXML private TextField     tfTxnAccNumber;
    @FXML private ComboBox<String> cbTxnType;
    @FXML private TextField     tfTxnAmount;
    @FXML private Label         lblTxnMessage;

    @FXML private TableView<Transaction>            tblTransactions;
    @FXML private TableColumn<Transaction, String>  colTxnType;
    @FXML private TableColumn<Transaction, String>  colTxnAmount;
    @FXML private TableColumn<Transaction, String>  colTxnDesc;

    private final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    // ── Tab: Activity Log ─────────────────────────────────────────────────────
    @FXML private ListView<String> lstLog;
    private final ObservableList<String> logList = FXCollections.observableArrayList();

    // ── Header ────────────────────────────────────────────────────────────────
    @FXML private Label lblBankName;
    @FXML private Label lblStatus;

    // =========================================================================
    // Initialise
    // =========================================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Header
        lblBankName.setText(bank.getBankName());

        // Wire customer table
        colCustId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCustName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCustEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tblCustomers.setItems(customerList);
        tblCustomers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Wire account table
        colAccNumber.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colAccType.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        colAccOwner.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getOwner().getName()));
        colAccBalance.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("$%.2f", d.getValue().getBalance())));
        colAccExtra.setCellValueFactory(d ->
                new SimpleStringProperty(extraLabel(d.getValue())));
        tblAccounts.setItems(accountList);
        tblAccounts.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Wire transaction table
        colTxnType.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getType().name()));
        colTxnAmount.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("$%.2f", d.getValue().getAmount())));
        colTxnDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        tblTransactions.setItems(transactionList);

        // Wire log list
        lstLog.setItems(logList);

        // Account type combo
        cbAccType.setItems(FXCollections.observableArrayList("Savings Account", "Checking Account"));
        cbAccType.getSelectionModel().selectFirst();
        updateExtraHint();
        cbAccType.setOnAction(e -> updateExtraHint());

        // Transaction type combo
        cbTxnType.setItems(FXCollections.observableArrayList("Deposit", "Withdraw"));
        cbTxnType.getSelectionModel().selectFirst();

        // Load saved data
        BankDataReader.loadAll(bank);
        BankLogger.info("Application started — data loaded from files.");
        refreshAll();
        setStatus("Ready. Data loaded from files.", false);
    }

    // =========================================================================
    // Customer actions
    // =========================================================================

    @FXML
    private void onAddCustomer() {
        String name  = tfCustName.getText().trim();
        String id    = tfCustId.getText().trim();
        String email = tfCustEmail.getText().trim();

        if (name.isEmpty() || id.isEmpty() || email.isEmpty()) {
            showCustError("All fields are required.");
            return;
        }
        try {
            Customer c = new Customer(name, id, email);
            bank.addCustomer(c);
            BankDataWriter.saveAll(bank);
            BankLogger.success("Customer added: " + name + " [" + id + "]");
            refreshCustomers();
            clearCustFields();
            showCustSuccess("Customer '" + name + "' registered successfully.");
            setStatus("Customer added: " + name, false);
        } catch (BankException e) {
            BankLogger.error("Add customer failed: " + e.getMessage());
            showCustError(e.getMessage());
        }
    }

    @FXML
    private void onRemoveCustomer() {
        Customer selected = tblCustomers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showCustError("Select a customer from the table first.");
            return;
        }
        try {
            bank.removeCustomer(selected.getId());
            BankDataWriter.saveAll(bank);
            BankLogger.success("Customer removed: " + selected.getName() + " [" + selected.getId() + "]");
            refreshAll();
            showCustSuccess("Customer '" + selected.getName() + "' and all their accounts removed.");
            setStatus("Customer removed: " + selected.getName(), false);
        } catch (BankException e) {
            BankLogger.error("Remove customer failed: " + e.getMessage());
            showCustError(e.getMessage());
        }
    }

    @FXML
    private void onSearchCustomer() {
        String id = tfSearchCustId.getText().trim();
        if (id.isEmpty()) {
            refreshCustomers();
            showCustSuccess("Showing all customers.");
            return;
        }
        bank.findCustomer(id).ifPresentOrElse(c -> {
            customerList.setAll(c);
            showCustSuccess("Found: " + c.getName() + " | " + c.getEmail());
            BankLogger.info("Customer search: found " + id);
        }, () -> {
            customerList.clear();
            showCustError("No customer found with ID '" + id + "'.");
            BankLogger.warning("Customer search: not found — " + id);
        });
    }

    @FXML
    private void onSortCustomers() {
        customerList.setAll(bank.getCustomersSortedByName());
        showCustSuccess("Customers sorted by name (A → Z).");
        BankLogger.info("Customers sorted by name.");
    }

    // =========================================================================
    // Account actions
    // =========================================================================

    @FXML
    private void onAddAccount() {
        String accNum   = tfAccNumber.getText().trim();
        String ownerId  = tfAccOwnerId.getText().trim();
        String type     = cbAccType.getValue();
        String balStr   = tfAccBalance.getText().trim();
        String extraStr = tfAccExtra.getText().trim();

        if (accNum.isEmpty() || ownerId.isEmpty() || balStr.isEmpty() || extraStr.isEmpty()) {
            showAccError("All fields are required.");
            return;
        }

        double balance, extra;
        try {
            balance = Double.parseDouble(balStr);
            extra   = Double.parseDouble(extraStr);
        } catch (NumberFormatException e) {
            showAccError("Balance and extra parameter must be valid numbers.");
            return;
        }

        if (balance <= 0) { showAccError("Initial balance must be positive."); return; }
        if (extra < 0)    { showAccError("Extra parameter cannot be negative."); return; }

        Customer owner = bank.findCustomer(ownerId).orElse(null);
        if (owner == null) {
            showAccError("No customer found with ID '" + ownerId + "'.");
            return;
        }

        try {
            Account account;
            if ("Savings Account".equals(type)) {
                // extra = interest rate; if user enters e.g. 6 treat as 6%
                double rate = extra > 1.0 ? extra / 100.0 : extra;
                account = new SavingsAccount(accNum, balance, owner, rate);
            } else {
                account = new CheckingAccount(accNum, balance, owner, extra);
            }
            bank.openAccount(account);
            BankDataWriter.saveAll(bank);
            BankLogger.success("Account opened: " + accNum + " (" + type + ") for " + owner.getName());
            refreshAccounts();
            clearAccFields();
            showAccSuccess("Account '" + accNum + "' opened for " + owner.getName() + ".");
            setStatus("Account opened: " + accNum, false);
        } catch (BankException e) {
            BankLogger.error("Open account failed: " + e.getMessage());
            showAccError(e.getMessage());
        }
    }

    @FXML
    private void onRemoveAccount() {
        Account selected = tblAccounts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAccError("Select an account from the table first.");
            return;
        }
        try {
            bank.removeAccount(selected.getAccountNumber());
            BankDataWriter.saveAll(bank);
            BankLogger.success("Account closed: " + selected.getAccountNumber());
            refreshAll();
            showAccSuccess("Account '" + selected.getAccountNumber() + "' closed.");
            setStatus("Account closed: " + selected.getAccountNumber(), false);
        } catch (BankException e) {
            BankLogger.error("Close account failed: " + e.getMessage());
            showAccError(e.getMessage());
        }
    }

    @FXML
    private void onSearchAccount() {
        String id = tfSearchAccId.getText().trim();
        if (id.isEmpty()) {
            refreshAccounts();
            showAccSuccess("Showing all accounts.");
            return;
        }
        bank.findAccount(id).ifPresentOrElse(a -> {
            accountList.setAll(a);
            showAccSuccess("Found: " + a.getAccountType() + " | Balance: $" +
                    String.format("%.2f", a.getBalance()));
            BankLogger.info("Account search: found " + id);
        }, () -> {
            accountList.clear();
            showAccError("No account found with number '" + id + "'.");
            BankLogger.warning("Account search: not found — " + id);
        });
    }

    @FXML
    private void onSortAccounts() {
        accountList.setAll(bank.getAccountsSortedByBalance());
        showAccSuccess("Accounts sorted by balance (highest first).");
        BankLogger.info("Accounts sorted by balance.");
    }

    // =========================================================================
    // Transaction actions
    // =========================================================================

    @FXML
    private void onPerformTransaction() {
        String accNum  = tfTxnAccNumber.getText().trim();
        String type    = cbTxnType.getValue();
        String amtStr  = tfTxnAmount.getText().trim();

        if (accNum.isEmpty() || amtStr.isEmpty()) {
            showTxnError("Account number and amount are required.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amtStr);
        } catch (NumberFormatException e) {
            showTxnError("Amount must be a valid number.");
            return;
        }

        Account account = bank.findAccount(accNum).orElse(null);
        if (account == null) {
            showTxnError("No account found with number '" + accNum + "'.");
            return;
        }

        try {
            if ("Deposit".equals(type)) {
                account.deposit(amount);
                BankLogger.success("Deposit $" + String.format("%.2f", amount) + " → " + accNum);
                showTxnSuccess(String.format("Deposited $%.2f. New balance: $%.2f", amount, account.getBalance()));
            } else {
                account.withdraw(amount);
                BankLogger.success("Withdrawal $" + String.format("%.2f", amount) + " ← " + accNum);
                showTxnSuccess(String.format("Withdrew $%.2f. New balance: $%.2f", amount, account.getBalance()));
            }
            BankDataWriter.saveAll(bank);
            refreshAll();
            // Show this account's history
            transactionList.setAll(account.getHistory());
            setStatus("Transaction complete on " + accNum, false);
        } catch (BankException e) {
            BankLogger.error("Transaction failed on " + accNum + ": " + e.getMessage());
            showTxnError(e.getMessage());
        }
    }

    @FXML
    private void onLoadHistory() {
        String accNum = tfTxnAccNumber.getText().trim();
        if (accNum.isEmpty()) {
            showTxnError("Enter an account number to load its history.");
            return;
        }
        bank.findAccount(accNum).ifPresentOrElse(a -> {
            transactionList.setAll(a.getHistory());
            if (a.getHistory().isEmpty()) {
                showTxnSuccess("No transactions yet for account " + accNum + ".");
            } else {
                showTxnSuccess("Loaded " + a.getHistory().size() + " transaction(s) for " + accNum + ".");
            }
            BankLogger.info("History loaded for account " + accNum);
        }, () -> showTxnError("No account found with number '" + accNum + "'."));
    }

    // =========================================================================
    // Log tab
    // =========================================================================

    @FXML
    private void onRefreshLog() {
        Platform.runLater(() -> {
            logList.setAll(BankLogger.getRecentLogs());
        });
    }

    // =========================================================================
    // Refresh helpers
    // =========================================================================

    private void refreshAll() {
        refreshCustomers();
        refreshAccounts();
        refreshLog();
    }

    private void refreshCustomers() {
        customerList.setAll(bank.getAllCustomers());
    }

    private void refreshAccounts() {
        accountList.setAll(bank.getAllAccounts());
    }

    private void refreshLog() {
        Platform.runLater(() -> logList.setAll(BankLogger.getRecentLogs()));
    }

    // =========================================================================
    // UI helpers
    // =========================================================================

    private void updateExtraHint() {
        if ("Savings Account".equals(cbAccType.getValue())) {
            lblAccExtraHint.setText("Interest Rate (e.g. 0.05 or 5)");
        } else {
            lblAccExtraHint.setText("Overdraft Limit ($)");
        }
    }

    private String extraLabel(Account a) {
        if (a instanceof SavingsAccount sa) {
            return String.format("Rate: %.0f%%", sa.getInterestRate() * 100);
        } else if (a instanceof CheckingAccount ca) {
            return String.format("Overdraft: $%.2f", ca.getOverdraftLimit());
        }
        return "";
    }

    private void clearCustFields() {
        tfCustName.clear(); tfCustId.clear(); tfCustEmail.clear();
    }

    private void clearAccFields() {
        tfAccNumber.clear(); tfAccOwnerId.clear();
        tfAccBalance.clear(); tfAccExtra.clear();
    }

    private void showCustSuccess(String msg) { styleLabel(lblCustMessage, msg, false); }
    private void showCustError(String msg)   { styleLabel(lblCustMessage, msg, true);  }
    private void showAccSuccess(String msg)  { styleLabel(lblAccMessage,  msg, false); }
    private void showAccError(String msg)    { styleLabel(lblAccMessage,  msg, true);  }
    private void showTxnSuccess(String msg)  { styleLabel(lblTxnMessage,  msg, false); }
    private void showTxnError(String msg)    { styleLabel(lblTxnMessage,  msg, true);  }

    private void styleLabel(Label lbl, String msg, boolean isError) {
        lbl.setText(msg);
        lbl.setStyle(isError
                ? "-fx-text-fill: #e74c3c; -fx-font-weight: bold;"
                : "-fx-text-fill: #27ae60; -fx-font-weight: bold;");
    }

    private void setStatus(String msg, boolean isError) {
        Platform.runLater(() -> {
            lblStatus.setText(msg);
            lblStatus.setStyle(isError
                    ? "-fx-text-fill: #e74c3c;"
                    : "-fx-text-fill: #2ecc71;");
        });
    }
}
