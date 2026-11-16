package bj.restaurant.couts.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {
    private DatabaseConnection() {
    }

    public static Connection getConnection() throws IOException, SQLException {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IOException("Fichier database.properties introuvable");
            }
            properties.load(input);
        }
        return DriverManager.getConnection(
                properties.getProperty("database.url"),
                properties.getProperty("database.username"),
                properties.getProperty("database.password"));
    }
}