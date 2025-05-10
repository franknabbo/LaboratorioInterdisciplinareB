package org.example.bookreccomender2.controller;

import org.example.bookreccomender2.Book;
import org.example.bookreccomender2.Rating;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class RatingController {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    private AlertController alertController = new AlertController();
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public RatingController() {
    }

    public boolean addRating(int idLibro, int styleRating, int contentRating, int appealRating,
                             int originalityRating, int editionRating, String reviewText, int averageRating) {
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Leggi il messaggio di benvenuto
            String welcome = in.readLine();

            //metti tra "" il testo della recensione
            reviewText = "\"" + reviewText + "\"";


            // Invia richiesta di creazione rating
            out.println("ADD_RATING:" + UserManager.getUserId() + ":" + idLibro + ":" + styleRating + ":"
                    + contentRating + ":" + appealRating + ":" + originalityRating + ":" + editionRating + ":"
                    + averageRating + ":" + reviewText);

            String response = in.readLine();
            // Gestisce la risposta
            if (response.startsWith("RATING_SUCCESS")) {
                alertController.showAlertSucces("Recensione aggiunta", "La recensione è stata aggiunta con successo");
                return true;
            } else {

                alertController.showAlert("Errore pubblicazione recensione", response.split(":", 2)[1]);
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Rating> fetchRating(int idLibro) {
        List<Rating> results = new ArrayList<>();
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            // Leggi il messaggio di benvenuto
            String welcome = in.readLine();

            // Invia richiesta di creazione rating
            out.println("GET_RATING:" + idLibro);

            String response = in.readLine();

            boolean reading = false;

            while ((line = in.readLine()) != null) {
                if (line.equals("INIZIO_LISTA_RATING")) {
                    reading = true;
                    continue;
                }

                if (line.equals("END_RATINGS")) {
                    break;
                }

                if (reading && line.startsWith("RATING:")) {
                    try {
                        String[] parts = line.split("RATING::(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");
                        if (parts.length >= 10) {
                            String idUtente = parts[1];
                            int stile = Integer.parseInt(parts[3]);
                            int contenuto = Integer.parseInt(parts[4]);
                            int gradevolezza = Integer.parseInt(parts[5]);
                            int originalita = Integer.parseInt(parts[6]);
                            int edizione = Integer.parseInt(parts[7]);
                            int votoFinale = Integer.parseInt(parts[8]);
                            String recensione = parts[9].replace("\"", "");

                            Rating rating = new Rating(idUtente, idLibro, stile, contenuto, gradevolezza, originalita, edizione, votoFinale, recensione);
                            results.add(rating);
                        } else {
                            System.err.println("Formato rating non valido: " + line);
                        }
                    } catch (Exception e) {
                        System.err.println("Errore nel parsing: " + e.getMessage());
                    }
                }
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return results;
    }
}