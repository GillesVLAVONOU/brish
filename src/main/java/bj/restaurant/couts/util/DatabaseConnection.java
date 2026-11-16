package bj.restaurant.couts.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public final class DatabaseConnection {
    private static final Properties CONFIGURATION = loadConfiguration();

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            DataSource dataSource = (DataSource) new InitialContext().lookup("java:/jdbc/BrishDS");
            return dataSource.getConnection();
        } catch (NamingException exception) {
            return getDriverManagerConnection(exception);
        }
    }

    private static Connection getDriverManagerConnection(NamingException cause) throws SQLException {
        String url = getSetting("BRISH_DB_URL", "brish.db.url", "database.url");
        String username = getSetting("BRISH_DB_USER", "brish.db.user", "database.username");
        String password = getSetting("BRISH_DB_PASSWORD", "brish.db.password", "database.password");

        if (url == null || username == null || password == null) {
            throw new SQLException(
                    "Configurez le datasource java:/jdbc/BrishDS ou BRISH_DB_URL, BRISH_DB_USER et BRISH_DB_PASSWORD.",
                    cause);
        }

        return DriverManager.getConnection(url, username, password);
    }

    private static Properties loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConnection.class.getResourceAsStream("/database.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
        return properties;
    }

    private static String getSetting(String environmentName, String systemProperty, String configurationKey) {
        String value = System.getenv(environmentName);
        if (value == null || value.isBlank()) {
            value = System.getProperty(systemProperty);
        }
        if (value == null || value.isBlank()) {
            value = CONFIGURATION.getProperty(configurationKey);
        }
        return value == null || value.isBlank() || value.equals("CHANGE_ME") ? null : value;
    }
}
