// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.bookrecommender2;

import org.example.bookrecommender2.controller.AlertController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Gestisce la connessione socket al server per l'invio e la ricezione di messaggi.
 * Usa un socket TCP con stream di input/output.
 */
public class SocketConnection {

    /** Socket usato per la connessione al server. */
    private static Socket socket;

    /** Stream di input per leggere i messaggi dal server. */
    private static BufferedReader in;

    /** Stream di output per inviare messaggi al server. */
    private static PrintWriter out;

    /** Controller per la gestione di alert GUI. */
    private static final AlertController alertController = new AlertController();

    /**
     * Stabilisce una connessione al server all'indirizzo e porta specificati.
     * In caso di errore di connessione mostra un alert e termina il programma.
     *
     * @param host indirizzo host del server
     * @param port numero di porta del server
     * @throws IOException in caso di errori di I/O nella creazione della connessione
     */
    public static void connect(String host, int port) throws IOException {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (ConnectException e) {
            alertController.showAlert("Errore connessione al server",
                    "Impossibile connettersi al server, controllare la connessione di rete");
            System.exit(1);
        }
    }

    /**
     * Restituisce lo stream di input per leggere messaggi dal server.
     *
     * @return BufferedReader per leggere messaggi
     */
    public static BufferedReader getIn() {
        return in;
    }

    /**
     * Invia un messaggio al server tramite lo stream di output.
     *
     * @param message messaggio da inviare
     */
    public static void sendMessage(String message) {
        out.println(message);
    }

    /**
     * Chiude la connessione socket e gli stream associati.
     *
     * @throws IOException in caso di errori durante la chiusura
     */
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
