// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.bookrecommender2.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import org.example.bookrecommender2.SocketConnection;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class UserManager {
    private static boolean loggedIn = false;
    private static String userId = null;
    private final SceneController sceneController = new SceneController();

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static String getUserId() {
        return userId;
    }

    public static void login(String userId) {
        UserManager.loggedIn = true;
        UserManager.userId = userId;
    }

    public static void logout() {
        UserManager.loggedIn = false;
        UserManager.userId = null;
    }

    public void loginUser(String userId, String password, ActionEvent event) throws IOException {

        if (userId.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Campi mancanti");
            alert.setContentText("Inserisci userId e password.");
            alert.showAndWait();
            return;
        }

        // Crittografia della password
        String encryptedPassword = encryptPassword(password);

        // Connessione al server
        try {


            // Invio della richiesta di login
            SocketConnection.sendMessage("LOGIN:" + userId + ":" + encryptedPassword);
            BufferedReader in = SocketConnection.getIn();
            // Gestione della risposta
            String risposta = in.readLine();

            if (risposta.startsWith("LOGIN OK")) {
                // Estrai l'userId dalla risposta (formato: "LOGIN OK:userId")
                String loggedUserId = risposta.split(":")[1];

                // Imposta lo stato di login
                UserManager.login(loggedUserId);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Completato");
                alert.setHeaderText("Login avvenuto con successo!");
                alert.setContentText("Benvenuto, " + loggedUserId);
                alert.showAndWait();

                // Reindirizza alla home page dopo il login
                sceneController.switchToHome(event);
            } else {
                String errorMessage = "Credenziali non valide.";
                if (risposta.contains(":")) {
                    errorMessage = risposta.split(":", 2)[1];
                }

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Login fallito");
                alert.setContentText(errorMessage);
                alert.showAndWait();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di connessione");
            alert.setHeaderText("Impossibile connettersi al server");
            alert.setContentText("Dettagli: " + e.getMessage());
            alert.showAndWait();

            System.out.println("Errore di connessione al server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void registerUser(ActionEvent event, String nome, String cognome, String codiceFiscale, String email, String password) {
        // Connessione al server
        try {
            String encryptedPassword = encryptPassword(password);

            SocketConnection.sendMessage("REGISTER:" + nome + ":" + cognome + ":" + codiceFiscale + ":" + email + ":" + encryptedPassword);
            // Gestione della risposta
            BufferedReader in = SocketConnection.getIn();
            // Gestione della risposta
            String risposta = in.readLine();
            System.out.println("Risposta dal server: " + risposta);

            if (risposta.startsWith("REGISTRAZIONE OK")) {
                // Estrai l'userId dalla risposta
                String userId = risposta.split(":")[1];

                UserManager.login(userId);

                // Mostra alert di successo con userId
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registrazione completata");
                alert.setHeaderText("Registrazione avvenuta con successo!");
                alert.setContentText("Il tuo userId è: " + userId + "\n\nUtilizza questo userId per accedere al sistema.");
                alert.showAndWait();

                // Reindirizza alla pagina di login
                sceneController.switchToHome(event);
            } else {
                // Mostra alert di errore
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Registrazione fallita");
                alert.setContentText("Non è stato possibile completare la registrazione.\n" + (risposta.contains(":") ? risposta.split(":", 2)[1] : ""));
                alert.showAndWait();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di connessione");
            alert.setHeaderText("Impossibile connettersi al server");
            alert.setContentText("Dettagli: " + e.getMessage());
            alert.showAndWait();

            System.out.println("Errore di connessione al server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Metodo per crittografare la password con SHA-256
    public String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Fallback in caso di errore
        }
    }
}