// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como
package bookrecommender.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Controller per il dialogo di creazione/modifica di una libreria.
 * Gestisce l'input del nome della libreria da parte dell'utente.
 */
public class LibraryDialogController {

    @FXML
    private TextField libraryNameField;

    /**
     * Restituisce il testo attualmente inserito nel campo per il nome della libreria.
     *
     * @return il nome della libreria inserito dall'utente
     */
    public String getLibraryNameField() {
        return libraryNameField.getText();
    }

}
