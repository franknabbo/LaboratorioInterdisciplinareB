package org.example.db;
import java.sql.*;

public class DataBaseConnection {
    // Configurazione del database con porta modificabile
    private static final String HOST = "localhost";
    private static final String PORT = "5432"; // Modifica qui se necessario
    private static final String DB_NAME = "BookRecommender";
    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";

    private Connection connection;

    // Costruttore per aprire la connessione
    public DataBaseConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Errore di connessione al database: " + e.getMessage());

            // Informazioni di debug più dettagliate
            if (e.getMessage().contains("Connection refused")) {
                System.err.println("Controlla che PostgreSQL sia in esecuzione sulla porta " + PORT);
            } else if (e.getMessage().contains("password authentication failed")) {
                System.err.println("Username o password non validi");
            } else if (e.getMessage().contains("database \"" + DB_NAME + "\" does not exist")) {
                System.err.println("Il database '" + DB_NAME + "' non esiste");
            }

            e.printStackTrace();
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
            System.err.println("Errore nella chiusura della connessione: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}