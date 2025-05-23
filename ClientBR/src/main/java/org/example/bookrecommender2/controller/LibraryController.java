// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como
package org.example.bookrecommender2.controller;

import org.example.bookrecommender2.Book;
import org.example.bookrecommender2.SocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la gestione delle librerie utente.
 * Permette di creare librerie, aggiungere libri a librerie esistenti
 * e ottenere la lista delle librerie di un utente.
 */
public class LibraryController {
    private AlertController alertController = new AlertController();

    /**
     * Crea una nuova libreria con il nome specificato per l'utente corrente.
     * Invia una richiesta al server e gestisce la risposta mostrando opportuni alert.
     *
     * @param libraryName il nome della libreria da creare
     * @return true se la libreria è stata creata con successo, false altrimenti
     */
    public boolean createLibraryWithName(String libraryName) {
        try {
            // Invia richiesta di creazione libreria
            SocketConnection.sendMessage("CREATE_LIBRARY:" + UserManager.getUserId() + ":" + libraryName);

            BufferedReader in = SocketConnection.getIn();

            // Gestisci la risposta
            String response = in.readLine();

            if (response.startsWith("LIBRARY_CREATED")) {
                alertController.showAlertSucces("Libreria creata", "La libreria '" + libraryName + "' è stata creata con successo.");
                return true;
            } else if (response.startsWith("LIBRARY_EXISTS")) {
                alertController.showAlert("Libreria già esistente", "La libreria '" + libraryName + "' esiste già.");
                return false;
            } else {
                String errorMessage = "Errore nella creazione della libreria.";
                if (response.contains(":")) {
                    errorMessage = response.split(":", 2)[1];
                }
                alertController.showAlert("Errore", errorMessage);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            alertController.showAlert("Errore di connessione", "Impossibile connettersi al server: " + e.getMessage());
            return false;
        }
    }

    /**
     * Aggiunge un libro selezionato alla libreria specificata.
     * Se il libro non è selezionato, mostra un alert di errore.
     * Comunica con il server e mostra alert in base alla risposta.
     *
     * @param libraryName il nome della libreria a cui aggiungere il libro
     * @param selectedBook il libro da aggiungere
     */
    public void addBookToSelectedLibrary(String libraryName, Book selectedBook) {
        if (selectedBook == null) {
            alertController.showAlert("Errore", "Nessun libro selezionato");
            return;
        }

        try {
            SocketConnection.sendMessage("ADD_BOOK_TO_LIBRARY:" + UserManager.getUserId() + ":" + libraryName + ":" + selectedBook.getId());

            BufferedReader in = SocketConnection.getIn();

            // Gestisci la risposta
            String response = in.readLine();

            if (response.startsWith("BOOK_ADDED")) {
                alertController.showAlertSucces("Libro aggiunto", "Il libro è stato aggiunto alla libreria '" + libraryName + "' con successo.");
            } else if (response.startsWith("BOOK_EXISTS")) {
                alertController.showAlert("Libro già presente", "Il libro è già presente nella libreria '" + libraryName + "'.");
            } else {
                String errorMessage = "Errore nell'aggiunta del libro.";
                if (response.contains(":")) {
                    errorMessage = response.split(":", 2)[1];
                }
                alertController.showAlert("Errore", errorMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
            alertController.showAlert("Errore di connessione", "Impossibile connettersi al server: " + e.getMessage());
        }
    }

    /**
     * Recupera la lista delle librerie associate all'utente corrente dal server.
     *
     * @return una lista di nomi di librerie
     */
    public List<String> getLibraryList() {
        List<String> libraries = new ArrayList<>();
        try {
            // Invia richiesta di librerie
            SocketConnection.sendMessage("GET_LIBRARY:" + UserManager.getUserId());
            BufferedReader in = SocketConnection.getIn();
            // Leggi la risposta riga per riga fino a "END_LIBRARIES"
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("END_LIBRARIES")) {
                    break;
                }
                libraries.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return libraries;
    }

}
