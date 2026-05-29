package com.barangay.controllers;

import com.barangay.MainApp;
import com.barangay.config.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    /**
     * Called when the Login button is clicked.
     * Validates input then checks credentials against the database.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        if (authenticateUser(username, password)) {
            try {
                MainApp.showDashboardScene();
            } catch (IOException e) {
                errorLabel.setText("Failed to load dashboard. Check console.");
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Invalid username or password.");
            passwordField.clear();
        }
    }


    private boolean authenticateUser(String username, String password) {
        // Only the username travels to the DB; we never pass the password in SQL.
        String sql = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    // BCrypt.checkpw hashes the candidate and compares — safe against timing attacks
                    return BCrypt.checkpw(password, storedHash);
                }
            }

        } catch (SQLException e) {
            errorLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
