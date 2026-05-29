package com.barangay;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApp extends Application {

    // Static reference to the primary stage so controllers can swap scenes
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showLoginScene();

        primaryStage.setTitle("Barangay Pooc Resident Information System");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Loads and displays the Login scene.
     * Called on startup and after logout.
     */
    public static void showLoginScene() throws IOException {
        Parent root = FXMLLoader.load(
            Objects.requireNonNull(
                MainApp.class.getResource("/com/barangay/login.fxml")
            )
        );
        primaryStage.setScene(new Scene(root, 400, 450));
        primaryStage.setTitle("Barangay System - Login");
    }

    /**
     * Loads and displays the Dashboard scene.
     * Called by LoginController upon successful authentication.
     */
    public static void showDashboardScene() throws IOException {
        Parent root = FXMLLoader.load(
            Objects.requireNonNull(
                MainApp.class.getResource("/com/barangay/dashboard.fxml")
            )
        );
        primaryStage.setScene(new Scene(root, 800, 550));
        primaryStage.setTitle("Barangay Resident Information System - Dashboard");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
