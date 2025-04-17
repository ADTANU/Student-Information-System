package test;

public class TestDriver {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load driver: " + e.getMessage());
        }
    }
}

