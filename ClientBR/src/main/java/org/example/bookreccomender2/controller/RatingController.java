package org.example.bookreccomender2.controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RatingController {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    private AlertController alertController = new AlertController();
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public RatingController() {
    }

    public boolean addRating(String idUtente, int idLibro, int styleRating, int contentRating, int appealRating,
                             int originalityRating, int editionRating, String reviewText, int averageRating) {
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Leggi il messaggio di benvenuto
            String welcome = in.readLine();

            // Invia richiesta di creazione rating
            out.println("ADD_RATING:" + UserManager.getUserId() + ":" + idLibro + ":" + styleRating + ":"
                    + contentRating + ":" + appealRating + ":" + originalityRating + ":" + editionRating + ":"
                    + reviewText + ":" + averageRating);

            String response = in.readLine();
            // Gestisce la risposta
            if (response.startsWith("RATING_SUCCESS")) {
                alertController.showAlertSucces("Recensione aggiunta", "La recensione è stata aggiunta con successo");
                return true;
            } else {
                alertController.showAlert("Errore pubblicazione recensione", "La recensione non è stata aggiunta");
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}