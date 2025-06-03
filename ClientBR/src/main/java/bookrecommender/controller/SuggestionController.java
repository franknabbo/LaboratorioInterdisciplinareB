// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package bookrecommender.controller;

import bookrecommender.Book;
import bookrecommender.BookClient;
import bookrecommender.SocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * Controller per la gestione delle operazioni relative ai suggerimenti di libri.
 * Permette di aggiungere libri suggeriti per un dato libro di riferimento
 * e di recuperare la lista di libri suggeriti associati a un libro specifico.
 */
public class SuggestionController {

    /**
     * Costruttore di default.
     */
    public SuggestionController() {
    }

    /**
     * Aggiunge una lista di libri suggeriti per un libro di riferimento di un utente specifico.
     * Costruisce e invia un messaggio al server con i dati da aggiungere.
     *
     * @param userId           Identificativo dell'utente che aggiunge i suggerimenti.
     * @param idLibroReferenced ID del libro di riferimento per cui si suggeriscono altri libri.
     * @param idLibroSuggested  Lista degli ID dei libri suggeriti da associare.
     * @return true se il server conferma l'aggiunta con successo, false altrimenti.
     * @throws RuntimeException in caso di errore di comunicazione con il server.
     */
    public boolean addSuggestedBook(String userId, int idLibroReferenced, List<Integer> idLibroSuggested) {
        StringBuilder message = new StringBuilder("ADD_SUGGESTED_BOOK:" + userId + ":" + idLibroReferenced);
        for (int i = 0; i < idLibroSuggested.size(); i++) {
            message.append(":").append(idLibroSuggested.get(i));
        }
        try {
            SocketConnection.sendMessage(String.valueOf(message));
            BufferedReader in = SocketConnection.getIn();
            String response = in.readLine();
            return response.startsWith("SUGGESTION_SUCCESS");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Recupera la lista di libri suggeriti associati a un libro di riferimento.
     * Invia una richiesta al server e parsifica la risposta in oggetti {@link Book}.
     *
     * @param idLibroReferenced ID del libro di riferimento.
     * @return Lista di libri suggeriti.
     * @throws RuntimeException in caso di errore di comunicazione con il server.
     */
    public List<Book> getSuggestedBooks(int idLibroReferenced) {
        String message = "GET_SUGGESTED_BOOKS:" + idLibroReferenced;
        List<Book> books = new java.util.ArrayList<>();
        try {
            SocketConnection.sendMessage(message);
            BufferedReader in = SocketConnection.getIn();
            String line;
            boolean reading = false;
            while ((line = in.readLine()) != null) {
                if (line.equals("INIZIO_LISTA_LIBRI")) {
                    reading = true;
                    continue;
                }
                if (line.equals("END_BOOKS")) {
                    break;
                }
                if (reading && line.startsWith("BOOK:")) {
                    try {
                        // Formato atteso: BOOK:id|||titolo|||autore|||categoria|||editore|||anno_pubblicazione|||copertina
                        String[] parts = line.split("BOOK:|\\|\\|\\|");
                        if (parts.length >= 8) {
                            Book book = BookClient.getBook(parts);
                            books.add(book);
                        } else {
                            System.err.println("Formato libro non valido: " + line);
                        }
                    } catch (Exception e) {
                        System.err.println("Errore nel parsing dei dati del libro: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return books;
    }
}
