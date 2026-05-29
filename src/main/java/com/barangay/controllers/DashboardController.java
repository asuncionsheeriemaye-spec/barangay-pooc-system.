package com.barangay.controllers;

import com.barangay.MainApp;
import com.barangay.config.DatabaseConnection;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.*;

public class DashboardController {

    // ── TableView and its columns ────────────────────────────────────────────
    @FXML private TableView<Resident>             residentTable;
    @FXML private TableColumn<Resident, Integer>  colId;
    @FXML private TableColumn<Resident, String>   colName;
    @FXML private TableColumn<Resident, Integer>  colAge;
    @FXML private TableColumn<Resident, String>   colContact;

    // ── Input fields ─────────────────────────────────────────────────────────
    @FXML private TextField nameField;
    @FXML private TextField ageField;
    @FXML private TextField contactField;

    // ── Status label ─────────────────────────────────────────────────────────
    @FXML private Label statusLabel;

    // ── Backing data list bound to the TableView ─────────────────────────────
    private final ObservableList<Resident> residentList = FXCollections.observableArrayList();

    /**
     * Automatically called by JavaFX after all @FXML fields are injected.
     * Sets up table columns and loads data from the database.
     */
    @FXML
    public void initialize() {
        // Wire each column to the matching Resident property
        colId.setCellValueFactory(
            data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colName.setCellValueFactory(
            data -> new SimpleStringProperty(data.getValue().getName()));
        colAge.setCellValueFactory(
            data -> new SimpleIntegerProperty(data.getValue().getAge()).asObject());
        colContact.setCellValueFactory(
            data -> new SimpleStringProperty(data.getValue().getContact()));

        residentTable.setItems(residentList);
        loadResidents();
    }

    // ── Database Operations ───────────────────────────────────────────────────

    /** Fetches all rows from the residents table and populates the TableView. */
    private void loadResidents() {
        residentList.clear();
        String sql = "SELECT id, name, age, contact FROM residents ORDER BY id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                residentList.add(new Resident(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age"),
                    rs.getString("contact")
                ));
            }
            setStatus("Loaded " + residentList.size() + " resident(s).", false);

        } catch (SQLException e) {
            setStatus("Load error: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    /** Inserts a new resident record using input field values. */
    @FXML
    private void handleAdd() {
        String name    = nameField.getText().trim();
        String ageText = ageField.getText().trim();
        String contact = contactField.getText().trim();

        // Input validation
        if (name.isEmpty() || ageText.isEmpty()) {
            setStatus("Name and Age are required fields.", true);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
            if (age <= 0 || age > 130) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            setStatus("Age must be a valid number (1–130).", true);
            return;
        }

        String sql = "INSERT INTO residents (name, age, contact) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setString(3, contact.isEmpty() ? null : contact);
            stmt.executeUpdate();

            clearFields();
            loadResidents();
            setStatus("Resident '" + name + "' added successfully.", false);

        } catch (SQLException e) {
            setStatus("Add error: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    /** Deletes the currently selected resident row by its database ID. */
    @FXML
    private void handleDelete() {
        Resident selected = residentTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            setStatus("Please select a resident to delete.", true);
            return;
        }

        // Confirmation dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete resident \"" + selected.getName() + "\"? This cannot be undone.",
            ButtonType.YES, ButtonType.CANCEL);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.YES) return;

        String sql = "DELETE FROM residents WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, selected.getId());
            stmt.executeUpdate();

            loadResidents();
            setStatus("Resident '" + selected.getName() + "' deleted.", false);

        } catch (SQLException e) {
            setStatus("Delete error: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    /** Refreshes the table from the database. */
    @FXML
    private void handleRefresh() {
        loadResidents();
    }

    /** Logs out and returns to the Login screen. */
    @FXML
    private void handleLogout() {
        try {
            MainApp.showLoginScene();
        } catch (IOException e) {
            setStatus("Failed to return to login screen.", true);
            e.printStackTrace();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void clearFields() {
        nameField.clear();
        ageField.clear();
        contactField.clear();
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(isError ? "-fx-text-fill: #cc0000;" : "-fx-text-fill: #007700;");
    }

    // ── Inner Model Class ─────────────────────────────────────────────────────

    /**
     * Simple POJO representing a single row in the residents table.
     * Used as the TableView's data model.
     */
    public static class Resident {
        private final int    id;
        private final String name;
        private final int    age;
        private final String contact;

        public Resident(int id, String name, int age, String contact) {
            this.id      = id;
            this.name    = name;
            this.age     = age;
            this.contact = contact;
        }

        public int    getId()      { return id; }
        public String getName()    { return name; }
        public int    getAge()     { return age; }
        public String getContact() { return contact; }
    }
}
