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

public class SocketConnection {
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;
    private static final AlertController alertController = new AlertController();

    public static void connect(String host, int port) throws IOException {
        try {socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);}catch (ConnectException e){
            alertController.showAlert("Errore connessione al server", "Impossibile connettersi al server, controllare la connessione di rete");
            System.exit(1);
        }
    }

    public static BufferedReader getIn() {
        return in;
    }


    public static void sendMessage(String message) {
        out.println(message);
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
