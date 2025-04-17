package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnUtil {

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load properties from db.properties
            Properties props = new Properties();
            props.load(DBConnUtil.class.getClassLoader().getResourceAsStream("db.properties"));

            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            String driver = props.getProperty("db.driver");

            // Load JDBC driver
            Class.forName(driver);

            // Establish database connection
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            System.err.println("Error establishing database connection: " + e.getMessage());
            connection = null;
        }
        return connection;
    }
}
