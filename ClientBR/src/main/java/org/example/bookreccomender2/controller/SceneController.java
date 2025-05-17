// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.bookreccomender2.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.bookreccomender2.Book;
import org.example.bookreccomender2.EventHandler;

import java.io.IOException;




public class SceneController {


    private AlertController alertController = new AlertController();
    public static String currentPage = "homeNotLogged-view.fxml";


    public void switchToRegister(ActionEvent event) {
        currentPage = "register-view.fxml";
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/bookreccomender2/register-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void switchToHome(ActionEvent event) {
        currentPage = "homeNotLogged-view.fxml";
        try {
            String viewFile = UserManager.isLoggedIn() ?
                    "/org/example/bookreccomender2/homeLogged-view.fxml" :
                    "/org/example/bookreccomender2/homeNotLogged-view.fxml";

            Parent root = FXMLLoader.load(getClass().getResource(viewFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void switchToLogin(ActionEvent event) {
        currentPage = "login-view.fxml";
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/bookreccomender2/login-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToBookView(MouseEvent event, Book selectedBook) {
        currentPage = "book-view.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookreccomender2/book-view.fxml"));
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

    public void switchToLibrary(ActionEvent event) {
        currentPage = "library-view.fxml";
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/bookreccomender2/library-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToLibraryBooks(MouseEvent event, String libraryName) {
        currentPage = "library-books-view.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookreccomender2/library-books-view.fxml"));
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

    public void switchToSuggestedBookList(ActionEvent event) {
        currentPage = "suggested-books-view.fxml";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookreccomender2/suggest-book-view.fxml"));
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
