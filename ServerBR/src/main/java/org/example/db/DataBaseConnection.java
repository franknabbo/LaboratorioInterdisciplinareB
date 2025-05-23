// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como


package org.example.db;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Scanner;

/**
 * Classe per gestire la connessione al database PostgreSQL,
 * con configurazione dinamica di host, porta, nome database,
 * utente e password.
 *
 * Fornisce metodi per aprire e chiudere la connessione,
 * verificare l'esistenza delle tabelle, creare tabelle e inserire dati
 * da file SQL, e testare la connessione.
 */
public class DataBaseConnection {
    /**
     * Host del server PostgreSQL.
     */
    private static String HOST;

    /**
     * Porta del server PostgreSQL.
     */
    private static String PORT;

    /**
     * Nome del database a cui connettersi.
     */
    private static String DB_NAME;

    /**
     * Username per l'autenticazione.
     */
    private static String USER;

    /**
     * Password per l'autenticazione.
     */
    private static String PASSWORD;

    /**
     * Connessione JDBC aperta con il database.
     */
    private Connection connection;

    /**
     * Costruttore che apre la connessione con il database usando i parametri configurati.
     * Se le tabelle non esistono, chiama il metodo per crearle e inserire i dati iniziali.
     * In caso di errore stampa messaggi dettagliati di debug.
     */
    public DataBaseConnection() {
        try {
            connection = DriverManager.getConnection(getURL(), USER, PASSWORD);
            if (!checkTablesExist()) {
                createTablesAndInsert();
            }
        } catch (SQLException e) {
            System.err.println("Errore di connessione al database: " + e.getMessage());

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

    /**
     * Restituisce l'URL JDBC completo, costruito da host, porta e nome database.
     *
     * @return URL completo per la connessione JDBC
     */
    private static String getURL() {
        return "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;
    }

    /**
     * Metodo statico per testare se la connessione con i parametri configurati funziona.
     *
     * @return true se la connessione è riuscita, false altrimenti
     */
    public static boolean testConnection() {
        try (Connection testConnection = DriverManager.getConnection(getURL(), USER, PASSWORD)) {
            return testConnection != null;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Crea le tabelle e inserisce i dati nel database eseguendo gli script SQL
     * letti da file esterni nelle risorse.
     */
    public void createTablesAndInsert() {
        try {
            String createTablesSql = readResourceFile("/sql/create_tables.sql");
            String insertDataSql = readResourceFile("/sql/insert_queries.sql");

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
     * Legge il contenuto di un file di risorsa SQL come stringa.
     *
     * @param resourcePath percorso relativo del file SQL nelle risorse
     * @return contenuto del file SQL come stringa
     * @throws IOException se la lettura fallisce
     */
    private String readResourceFile(String resourcePath) throws IOException {
        String filePath = System.getProperty("user.dir") + "\\ServerBR" + resourcePath;
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

    /**
     * Controlla se la tabella 'libri' esiste nel database interrogando
     * il catalogo delle tabelle di PostgreSQL.
     *
     * @return true se la tabella 'libri' esiste, false altrimenti
     */
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

    /**
     * Chiude la connessione aperta con il database.
     */
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

    /**
     * Restituisce la connessione JDBC aperta.
     *
     * @return oggetto Connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Restituisce l'username configurato.
     *
     * @return username
     */
    public static String getUSER() {
        return USER;
    }

    /**
     * Imposta l'username per la connessione al database.
     *
     * @param USER username da impostare
     */
    public static void setUSER(String USER) {
        DataBaseConnection.USER = USER;
    }

    /**
     * Restituisce la password configurata.
     *
     * @return password
     */
    public static String getPASSWORD() {
        return PASSWORD;
    }

    /**
     * Imposta la password per la connessione al database.
     *
     * @param PASSWORD password da impostare
     */
    public static void setPASSWORD(String PASSWORD) {
        DataBaseConnection.PASSWORD = PASSWORD;
    }

    /**
     * Restituisce l'host configurato.
     *
     * @return host
     */
    public static String getHOST() {
        return HOST;
    }

    /**
     * Imposta l'host per la connessione al database.
     *
     * @param HOST host da impostare
     */
    public static void setHOST(String HOST) {
        DataBaseConnection.HOST = HOST;
    }

    /**
     * Restituisce la porta configurata.
     *
     * @return porta
     */
    public static String getPORT() {
        return PORT;
    }

    /**
     * Imposta la porta per la connessione al database.
     *
     * @param PORT porta da impostare
     */
    public static void setPORT(String PORT) {
        DataBaseConnection.PORT = PORT;
    }

    /**
     * Restituisce il nome del database configurato.
     *
     * @return nome database
     */
    public static String getDbName() {
        return DB_NAME;
    }

    /**
     * Imposta il nome del database.
     *
     * @param dbName nome database da impostare
     */
    public static void setDbName(String dbName) {
        DB_NAME = dbName;
    }
}
