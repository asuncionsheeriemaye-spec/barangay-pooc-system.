package com.barangay.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides a JDBC connection to the Supabase PostgreSQL database.
 *
 * Credentials are loaded at runtime from the .env file in the project root.
 * They are NEVER hardcoded in source code.
 *
 * Setup: create a file called ".env" in the project root (same folder as pom.xml)
 * with these keys:
 *   DB_URL=jdbc:postgresql://...
 *   DB_USER=postgres.xxxx
 *   DB_PASSWORD=your_password
 */
public class DatabaseConnection {

    // Loads the .env file from the project root (where pom.xml lives).
    // ignoreIfMissing() prevents a crash on CI/CD where env vars may come
    // from the system environment instead of a file.
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./")          // project root
            .ignoreIfMissing()
            .load();

    private static final String DB_URL  = dotenv.get("DB_URL");
    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASS = dotenv.get("DB_PASSWORD");

    /**
     * Opens and returns a new database connection.
     * The caller is responsible for closing it (use try-with-resources).
     *
     * @return an active {@link Connection}
     * @throws SQLException if credentials are missing or the connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (DB_URL == null || DB_USER == null || DB_PASS == null) {
            throw new SQLException(
                "Missing database credentials. " +
                "Please ensure a .env file exists in the project root " +
                "with DB_URL, DB_USER, and DB_PASSWORD defined."
            );
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found on classpath.", e);
        }

        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
