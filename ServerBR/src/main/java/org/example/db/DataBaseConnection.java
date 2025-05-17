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
    private static final String HOST = "localhost";
    private static final String PORT = "5432"; // Modifica qui se necessario
    private static final String DB_NAME = "BookRecommender";
    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;
    private static String USER;
    private static String PASSWORD;

    private Connection connection;

    // Costruttore per aprire la connessione
    public DataBaseConnection() {
        try {
            //Get User password from input
            Scanner scanner = new Scanner(System.in);
            System.out.print("Inserisci il nome utente per il database: ");
            USER = scanner.nextLine();
            System.out.print("Inserisci la password per l'utente " + USER + ": ");
            PASSWORD = scanner.nextLine();
            // Connessione al database
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            if(!checkTablesExist()){
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
}