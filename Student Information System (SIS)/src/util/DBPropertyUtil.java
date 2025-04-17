package util;

import java.util.Properties;
import java.io.FileInputStream;

public class DBPropertyUtil {
    public static String getConnectionString(String filename) {
        try (FileInputStream fis = new FileInputStream(filename)) {
            Properties props = new Properties();
            props.load(fis);
            return props.getProperty("db.url") + "?user=" + props.getProperty("db.username") + "&password=" + props.getProperty("db.password");
        } catch (Exception e) {
            System.err.println("Error reading database properties file: " + e.getMessage());
            return null;
        }
    }
}