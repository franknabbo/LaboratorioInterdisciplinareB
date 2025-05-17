// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.example.db.Book;
import org.example.db.BookDAO;
import org.example.db.DataBaseConnection;

public class Main {
    public static final int PORT = 8080;

    public static void main(String[] args) {
        DataBaseConnection db = new DataBaseConnection();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
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

