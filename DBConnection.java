package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/task_scheduler";
    private static final String USER = "root";
    private static final String PASSWORD = "Shilpi@3005";

    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            return con;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}