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
import org.example.bookreccomender2.Rating;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SceneController {

    private AlertController alertController = new AlertController();

    public SceneController() {
    }


    public void switchToRegister(ActionEvent event) {
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
            List<Rating> list = new ArrayList<>();
            RatingController ratingController = new RatingController();

            // todo fa il metodo ratingController.fetchRatings(selectedBook.getId()) e mette i risultati in vista

        } catch (IOException e) {
            e.printStackTrace();
            alertController.showAlert("Errore", "Impossibile aprire la pagina del libro: " + e.getMessage());
        }
    }

    public void switchToLibrary(ActionEvent event) {
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
}
