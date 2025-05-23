// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.bookrecommender2.controller;

import org.example.bookrecommender2.Rating;
import org.example.bookrecommender2.SocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller che gestisce l'aggiunta e il recupero delle recensioni (rating) dei libri.
 */
public class RatingController {
    private final AlertController alertController = new AlertController();

    /**
     * Aggiunge una nuova recensione per un libro specificato.
     *
     * @param idLibro          l'ID del libro da recensire
     * @param styleRating      voto per lo stile
     * @param contentRating    voto per il contenuto
     * @param appealRating     voto per la gradevolezza
     * @param originalityRating voto per l'originalità
     * @param editionRating    voto per l'edizione
     * @param reviewText       testo della recensione
     * @param averageRating    voto medio complessivo
     */
    public void addRating(int idLibro, int styleRating, int contentRating, int appealRating,
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
            } else {
                alertController.showAlert("Errore pubblicazione recensione", "La recensione non è stata pubblicata controlla di avere il libro in una delle tue librerie");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Recupera la lista delle recensioni associate a un dato libro.
     *
     * @param idLibro l'ID del libro di cui si vogliono ottenere le recensioni
     * @return lista di oggetti Rating contenenti le recensioni del libro
     */
    public List<Rating> fetchRating(int idLibro) {
        List<Rating> results = new ArrayList<>();
        try {
            // Invia richiesta di ottenimento recensioni
            SocketConnection.sendMessage("GET_RATING:" + idLibro);
            BufferedReader in = SocketConnection.getIn();

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
                        if (parts.length >= 10) { // 10 perché recensione è parts[9]
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
                        // Se si verifica un errore nella lettura di una recensione, interrompe la lettura e restituisce ciò che ha ottenuto finora
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
