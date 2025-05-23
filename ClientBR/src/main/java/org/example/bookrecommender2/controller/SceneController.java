// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.bookrecommender2.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.bookrecommender2.Book;
import org.example.bookrecommender2.EventHandler;

import java.io.IOException;

/**
 * Controller per la gestione della navigazione tra le varie scene dell'applicazione.
 * Fornisce metodi per passare da una vista all'altra, caricando i relativi file FXML
 * e inizializzando i controller associati quando necessario.
 */
public class SceneController {

    private final AlertController alertController = new AlertController();

    /**
     * Nome della pagina attualmente visualizzata.
     * Utilizzato per tenere traccia della vista attiva.
     */
    public static String currentPage = "homeNotLogged-view.fxml";

    /**
     * Nome della libreria attualmente selezionata.
     */
    public static String currentLibrary = "";

    /**
     * Passa alla scena di registrazione.
     * @param event evento generato dal click su un pulsante o altro controllo.
     */
    public void switchToRegister(ActionEvent event) {
        currentPage = "register-view.fxml";
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/bookrecommender2/register-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Passa alla scena principale (home).
     * Se l'utente è loggato, carica la home per utenti loggati,
     * altrimenti la home per utenti non loggati.
     * @param event evento generato dal click su un pulsante o altro controllo.
     */
    public void switchToHome(ActionEvent event) {
        currentPage = "homeNotLogged-view.fxml";
        try {
            String viewFile = UserManager.isLoggedIn() ?
                    "/org/example/bookrecommender2/homeLogged-view.fxml" :
                    "/org/example/bookrecommender2/homeNotLogged-view.fxml";

            Parent root = FXMLLoader.load(getClass().getResource(viewFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Passa alla scena di login.
     * @param event evento generato dal click su un pulsante o altro controllo.
     */
    public void switchToLogin(ActionEvent event) {
        currentPage = "login-view.fxml";
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/bookrecommender2/login-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Passa alla vista dettagliata di un libro selezionato.
     * Inizializza il controller della vista con i dati del libro.
     * @param event evento generato dal click su un elemento della lista libri.
     * @param selectedBook libro selezionato da mostrare nella nuova vista.
     */
    public void switchToBookView(MouseEvent event, Book selectedBook) {
        currentPage = "book-view.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookrecommender2/book-view.fxml"));
            Parent root = loader.load();

            // Ottieni il controller e inizializza i dati del libro
            EventHandler controller = loader.getController();
            controller.initBookData(selectedBook);

            // Cambia scena
            Scene scene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) scene.getWindow();
            scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            alertController.showAlert("Errore", "Impossibile aprire la pagina del libro: " + e.getMessage());
        }
    }

    /**
     * Passa alla scena della lista librerie.
     * @param event evento generato dal click su un pulsante o altro controllo.
     */
    public void switchToLibrary(ActionEvent event) {
        currentPage = "library-view.fxml";
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/bookrecommender2/library-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Passa alla vista dei libri di una libreria specifica.
     * Inizializza il controller con il nome della libreria selezionata.
     * @param event evento generato dal click su un elemento della lista librerie.
     * @param libraryName nome della libreria da visualizzare.
     */
    public void switchToLibraryBooks(MouseEvent event, String libraryName) {
        currentPage = "library-books-view.fxml";
        currentLibrary = libraryName;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookrecommender2/library-books-view.fxml"));
            Parent root = loader.load();

            // Ottieni il controller e inizializza i dati
            EventHandler controller = loader.getController();
            controller.initLibraryBooksView(libraryName);

            // Cambia scena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            alertController.showAlert("Errore", "Impossibile aprire la vista dei libri della libreria: " + e.getMessage());
        }
    }

    /**
     * Passa alla vista della lista di libri suggeriti.
     * @param event evento generato dal click su un pulsante o altro controllo.
     */
    public void switchToSuggestedBookList(ActionEvent event) {
        currentPage = "suggested-books-view.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookrecommender2/suggest-book-view.fxml"));
            Parent root = loader.load();

            // Ottieni il controller e inizializza i dati
            EventHandler controller = loader.getController();

            // Cambia scena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            alertController.showAlert("Errore", "Impossibile aprire la vista dei libri suggeriti: " + e.getMessage());
        }
    }
}
