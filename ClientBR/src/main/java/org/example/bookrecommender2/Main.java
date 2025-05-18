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

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        stage.setWidth(1000);
        stage.setHeight(800);

        SocketConnection.connect("localhost", 8080);

        Parent root = FXMLLoader.load(getClass().getResource("/org/example/bookrecommender2/homeNotLogged-view.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("BookRecomender");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}