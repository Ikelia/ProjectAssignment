package com.bank.ui.component;

import javafx.scene.control.Label;

/**
 * Reusable UI component — a coloured status badge label.
 *
 * Usage:
 *   StatusBadge badge = new StatusBadge();
 *   badge.showSuccess("Operation completed.");
 *   badge.showError("Something went wrong.");
 *   badge.clear();
 */
public class StatusBadge extends Label {

    public StatusBadge() {
        setWrapText(true);
        setStyle("-fx-font-size: 12px; -fx-padding: 4 8 4 8; -fx-background-radius: 4;");
    }

    public void showSuccess(String message) {
        setText(message);
        setStyle("-fx-font-size: 12px; -fx-padding: 4 8 4 8; -fx-background-radius: 4;"
               + "-fx-background-color: #14532d; -fx-text-fill: #4ade80; -fx-font-weight: bold;");
    }

    public void showError(String message) {
        setText(message);
        setStyle("-fx-font-size: 12px; -fx-padding: 4 8 4 8; -fx-background-radius: 4;"
               + "-fx-background-color: #450a0a; -fx-text-fill: #f87171; -fx-font-weight: bold;");
    }

    public void showInfo(String message) {
        setText(message);
        setStyle("-fx-font-size: 12px; -fx-padding: 4 8 4 8; -fx-background-radius: 4;"
               + "-fx-background-color: #1e3a5f; -fx-text-fill: #93c5fd; -fx-font-weight: bold;");
    }

    public void clear() {
        setText("");
        setStyle("-fx-font-size: 12px; -fx-padding: 4 8 4 8; -fx-background-radius: 4;");
    }
}
