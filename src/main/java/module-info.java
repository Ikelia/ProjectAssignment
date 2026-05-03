module com.bank {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.bank to javafx.fxml;
    opens com.bank.ui.controller to javafx.fxml;
    opens com.bank.ui.component to javafx.fxml;

    exports com.bank;
    exports com.bank.model;
    exports com.bank.exceptions;
    exports com.bank.fileio;
    exports com.bank.ui.controller;
    exports com.bank.ui.component;
    exports com.bank.logging;
}
