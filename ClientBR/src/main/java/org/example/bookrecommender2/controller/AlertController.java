// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como
package org.example.bookrecommender2.controller;

import javafx.scene.control.Alert;

/**
 * Controller per la gestione di finestre di dialogo di alert.
 * Consente di mostrare alert di errore e di successo all'utente.
 */
public class AlertController {

    /**
     * Costruttore di default.
     */
    public AlertController() {
    }

    /**
     * Mostra una finestra di alert di tipo errore con titolo e messaggio specificati.
     *
     * @param title il titolo della finestra di alert
     * @param message il messaggio di contenuto da mostrare
     */
    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Mostra una finestra di alert di tipo successo/informazione con titolo e messaggio specificati.
     *
     * @param title il titolo della finestra di alert
     * @param message il messaggio di contenuto da mostrare
     */
    public void showAlertSucces(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
