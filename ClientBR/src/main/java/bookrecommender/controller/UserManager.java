// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package bookrecommender.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import bookrecommender.SocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import  bookrecommender.controller.AlertController;
/**
 * Gestisce le operazioni di autenticazione dell'utente,
 * inclusi login, logout e registrazione,
 * nonché la gestione dello stato di autenticazione.
 */
public class UserManager {

    private static boolean loggedIn = false;
    private static String userId = null;
    private final SceneController sceneController = new SceneController();
    private final AlertController alertController = new AlertController();

    /**
     * Verifica se un utente è attualmente loggato.
     *
     * @return true se l'utente è loggato, false altrimenti.
     */
    public static boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Restituisce l'ID dell'utente attualmente loggato.
     *
     * @return l'ID utente se loggato, null altrimenti.
     */
    public static String getUserId() {
        return userId;
    }

    /**
     * Imposta lo stato di login a true e salva l'ID utente.
     *
     * @param userId l'ID utente da impostare.
     */
    public static void login(String userId) {
        UserManager.loggedIn = true;
        UserManager.userId = userId;
    }

    /**
     * Esegue il logout dell'utente resettando lo stato di login e l'ID utente.
     */
    public static void logout() {
        UserManager.loggedIn = false;
        UserManager.userId = null;
    }

    /**
     * Esegue la procedura di login inviando userId e password criptata al server.
     * Gestisce la risposta e mostra messaggi di alert in base al risultato.
     * Se il login ha successo, cambia scena verso la home.
     *
     * @param userId   l'ID utente inserito.
     * @param password la password in chiaro da criptare.
     * @param event    evento legato all'interfaccia utente (per cambiare scena).
     * @throws IOException in caso di errore di I/O con il server.
     */
    public void loginUser(String userId, String password, ActionEvent event) throws IOException {
        if (userId.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Campi mancanti");
            alert.setContentText("Inserisci userId e password.");
            alert.showAndWait();
            return;
        }
        String encryptedPassword = encryptPassword(password);

        try {
            SocketConnection.sendMessage("LOGIN:" + userId + ":" + encryptedPassword);
            BufferedReader in = SocketConnection.getIn();
            String risposta = in.readLine();

            if (risposta.startsWith("LOGIN OK")) {
                UserManager.login(userId);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Completato");
                alert.setHeaderText("Login avvenuto con successo!");
                alert.setContentText("Benvenuto, " + userId);
                alert.showAndWait();
                sceneController.switchToHome(event);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Login fallito");
                alert.setContentText("Credenziali non valide.");
                alert.showAndWait();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di connessione");
            alert.setHeaderText("Impossibile connettersi al server");
            alert.setContentText("Dettagli: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Esegue la procedura di registrazione inviando i dati al server.
     * Cripta la password, gestisce la risposta e mostra messaggi di alert.
     * Se la registrazione ha successo, effettua il login automatico e torna alla home.
     *
     * @param event        evento legato all'interfaccia utente (per cambiare scena).
     * @param nome         nome dell'utente.
     * @param cognome      cognome dell'utente.
     * @param codiceFiscale codice fiscale dell'utente.
     * @param email        email dell'utente.
     * @param password     password in chiaro da criptare.
     */
    public void registerUser(ActionEvent event, String nome, String cognome, String codiceFiscale, String email, String password) {
        try {
            String encryptedPassword = encryptPassword(password);
            SocketConnection.sendMessage("REGISTER:" + nome + ":" + cognome + ":" + codiceFiscale + ":" + email + ":" + encryptedPassword);
            BufferedReader in = SocketConnection.getIn();
            String risposta = in.readLine();
            System.out.println("Risposta dal server: " + risposta);

            if (risposta.startsWith("REGISTRAZIONE OK")) {
                String userId = risposta.split(":")[1];
                UserManager.login(userId);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registrazione completata");
                alert.setHeaderText("Registrazione avvenuta con successo!");
                alert.setContentText("Il tuo userId è: " + userId + "\n\nUtilizza questo userId per accedere al sistema.");
                alert.showAndWait();

                sceneController.switchToHome(event);
            } else if( risposta.startsWith("REGISTRAZIONE FALLITA:Codice fiscale o email già registrati")) {
                alertController.showAlert("Errore", "Codice fiscale o email già registrati. Prova con un altro codice fiscale o email.");

            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Registrazione fallita");
                alert.setContentText("Non è stato possibile completare la registrazione.\n");
                alert.showAndWait();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di connessione");
            alert.setHeaderText("Impossibile connettersi al server");
            alert.setContentText("Dettagli: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Cripta una password in chiaro usando SHA-256.
     *
     * @param password la password in chiaro da criptare.
     * @return la password criptata in formato esadecimale.
     */
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
