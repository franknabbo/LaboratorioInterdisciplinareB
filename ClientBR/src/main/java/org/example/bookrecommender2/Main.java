// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.bookrecommender2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale dell'applicazione BookRecommender.
 * Estende javafx.application.Application e gestisce l'avvio della UI.
 */
public class Main extends Application {

    /**
     * Metodo invocato all'avvio dell'applicazione JavaFX.
     * Configura la finestra principale, stabilisce la connessione socket,
     * carica la scena iniziale per utenti non loggati e mostra la finestra.
     *
     * @param stage Stage principale della JavaFX application.
     * @throws IOException se il caricamento della risorsa FXML fallisce.
     */
    @Override
    public void start(Stage stage) throws IOException {

        // Imposta dimensioni della finestra principale
        stage.setWidth(1000);
        stage.setHeight(800);

        // Stabilisce la connessione socket al server locale sulla porta 8080
        SocketConnection.connect("localhost", 8080);

        // Carica la UI iniziale per utenti non loggati
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/bookrecommender2/homeNotLogged-view.fxml"));
        Scene scene = new Scene(root);

        // Imposta titolo e scena della finestra
        stage.setTitle("BookRecomender");
        stage.setScene(scene);

        // Mostra la finestra
        stage.show();
    }

    /**
     * Metodo main che lancia l'applicazione JavaFX.
     *
     * @param args Argomenti da linea di comando (non utilizzati).
     */
    public static void main(String[] args) {
        launch();
    }
}