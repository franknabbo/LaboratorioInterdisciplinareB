package org.example.bookreccomender2.controller;

import org.example.bookreccomender2.Book;
import org.example.bookreccomender2.Rating;
import org.example.bookreccomender2.SocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class RatingController {
    private AlertController alertController = new AlertController();
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public RatingController() {
    }

    public boolean addRating(int idLibro, int styleRating, int contentRating, int appealRating,
                             int originalityRating, int editionRating, String reviewText, int averageRating) {
        try {

            // Invia richiesta di creazione rating
            SocketConnection.sendMessage("ADD_RATING:" + UserManager.getUserId() + "|||" + idLibro + "|||" + styleRating + "|||"
                    + contentRating + "|||" + appealRating + "|||" + originalityRating + "|||" + editionRating + "|||"
                    + averageRating + "|||" + reviewText);
            BufferedReader in = SocketConnection.getIn();
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
        try {
            // Invia richiesta di creazione rating
            out.println("GET_RATING:" + idLibro);

            // Leggi la risposta
             String line;
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
                        String[] parts = line.split("RATING:|\\|\\|\\|");
                        if (parts.length >= 9) {
                            String idUtente = parts[1];
                            int idLibroRating = Integer.parseInt(parts[2]);
                            int stile = Integer.parseInt(parts[3]);
                            int contenuto = Integer.parseInt(parts[4]);
                            int gradevolezza = Integer.parseInt(parts[5]);
                            int originalita = Integer.parseInt(parts[6]);
                            int edizione = Integer.parseInt(parts[7]);
                            int votoFinale = Integer.parseInt(parts[8]);
                            String recensione = parts[9];

                            Rating rating = new Rating(idUtente, idLibroRating, stile, contenuto, gradevolezza,
                                    originalita, edizione, votoFinale, recensione);
                            results.add(rating);
                        } else {
                            System.err.println("Formato rating non valido: " + line);
                        }




                    } catch (Exception e) {
                        return results;
                    }
                }
            }

            return results;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



