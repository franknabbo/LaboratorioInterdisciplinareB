package org.example.bookreccomender2;
import java.sql.*;

public class DataBaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/BookRecomender";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";
    private Connection connection;

    // Costruttore per aprire la connessione
    public DataBaseConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per eseguire query SELECT
    public ResultSet executeQuery(String query) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Metodo per eseguire query di UPDATE, INSERT, DELETE
    public int executeUpdate(String query) {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Metodo per chiudere la connessione
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connessione chiusa.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

}