package edu.persianlearningapp.dao.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Local XAMPP defaults; override with environment variables on each machine. */
public final class DatabaseConnection {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/persian_learning_app_team?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
    private DatabaseConnection() { }
    public static Connection open() throws SQLException {
        String url = value("PERSIAN_DB_URL", DEFAULT_URL);
        String user = value("PERSIAN_DB_USER", "root");
        String password = value("PERSIAN_DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, password);
    }
    private static String value(String key, String fallback) {
        String value = System.getenv(key);
        return value == null ? fallback : value;
    }
}
