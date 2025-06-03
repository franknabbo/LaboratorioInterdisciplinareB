// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package bookrecommender;
import bookrecommender.db.*;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


/**
 * Classe principale che avvia il server e gestisce le connessioni in arrivo.
 */
public class Main {
    /**
     * Porta su cui il server rimane in ascolto per le connessioni client.
     */
    public static final int PORT = 8080;

    /**
     * Metodo main che avvia il server, configura la connessione al database
     * e accetta connessioni client creando un thread per ciascuna.
     *
     * @param args argomenti da linea di comando (non utilizzati)
     */
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Scanner scanner = new Scanner(System.in);
            do {

                System.out.print("Inserisci l'host del database: ");
                String HOST = scanner.nextLine();
                System.out.print("Inserisci la porta del database: ");
                String DB_PORT = scanner.nextLine();
                System.out.print("Inserisci il nome del database: ");
                String DB_NAME = scanner.nextLine();
                DataBaseConnection.setHOST(HOST);
                DataBaseConnection.setPORT(DB_PORT);
                DataBaseConnection.setDbName(DB_NAME);
                System.out.print("Inserisci il nome utente per il database: ");
                String USER = scanner.nextLine();
                System.out.print("Inserisci la password per l'utente " + USER + ": ");
                String PASSWORD = scanner.nextLine();
                DataBaseConnection.setUSER(USER);
                DataBaseConnection.setPASSWORD(PASSWORD);

                if (!DataBaseConnection.testConnection())
                    System.out.println("Errore Credenziali Sbagliate");
            } while (!DataBaseConnection.testConnection());

            DataBaseConnection dbConnection = new DataBaseConnection();
            if (!dbConnection.checkTablesExist()) {
                dbConnection.createTablesAndInsert();
            }

            System.out.println("Server avviato sulla porta " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuova connessione accettata");

                // Ogni client gestito in un nuovo thread
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Errore nel server: " + e.getMessage());
        }
    }
}
