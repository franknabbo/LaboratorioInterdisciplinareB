// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.db;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Scanner;


public class DataBaseConnection {
    // Configurazione del database con porta modificabile
    private static String HOST;
    private static String PORT;
    private static String DB_NAME;
    private static String USER;
    private static String PASSWORD;

    // Rimuovere la definizione statica dell'URL
    // private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;

    private Connection connection;

    // Metodo per ottenere l'URL aggiornato
    private static String getURL() {
        return "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;
    }

    // Costruttore per aprire la connessione
    public DataBaseConnection() {
        try {
            // Connessione al database con URL aggiornato
            connection = DriverManager.getConnection(getURL(), USER, PASSWORD);
            if (!checkTablesExist()) {
                createTablesAndInsert();
            }
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

    //Metodo per provare la connessione se le crendenziali sono corrette
    public static boolean testConnection() {
        try (Connection testConnection = DriverManager.getConnection(getURL(), USER, PASSWORD)) {
            return testConnection != null;
        } catch (SQLException e) {
            return false;
        }
    }

    // Metodo per creare le tabelle
    public void createTablesAndInsert() {
        try {
            // Leggi le query dai file
            String createTablesSql = readResourceFile("/sql/create_tables.sql");
            String insertDataSql = readResourceFile("/sql/insert_queries.sql");

            // Esecuzione delle query
            try (Statement statement = connection.createStatement()) {
                statement.execute(createTablesSql);
                statement.execute(insertDataSql);
            }
        } catch (IOException | SQLException e) {
            System.err.println("Errore durante l'esecuzione delle query: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Legge il contenuto di un file dalle risorse
     *
     * @param resourcePath percorso del file nelle risorse
     * @return contenuto del file come stringa
     * @throws IOException se si verifica un errore di lettura
     */
    private String readResourceFile(String resourcePath) throws IOException {
        String filePath = System.getProperty("user.dir") + "\\LaboratorioInterdisciplinareB\\ServerBR" + resourcePath;
        File file = new File(filePath);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    //metodo per controllare se esistono le tabelle nel database
    public boolean checkTablesExist() {
        String sql = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'libri')";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            System.err.println("Errore nel controllo delle tabelle: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
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

    public static String getUSER() {
        return USER;
    }

    public static void setUSER(String USER) {
        DataBaseConnection.USER = USER;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public static void setPASSWORD(String PASSWORD) {
        DataBaseConnection.PASSWORD = PASSWORD;
    }

    public static String getHOST() {
        return HOST;
    }

    public static void setHOST(String HOST) {
        DataBaseConnection.HOST = HOST;
    }

    public static String getPORT() {
        return PORT;
    }

    public static void setPORT(String PORT) {
        DataBaseConnection.PORT = PORT;
    }

    public static String getDbName() {
        return DB_NAME;
    }

    public static void setDbName(String dbName) {
        DB_NAME = dbName;
    }
}