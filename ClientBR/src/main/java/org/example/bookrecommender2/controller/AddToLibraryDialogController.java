// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.bookrecommender2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import java.util.List;

/**
 * Controller per la finestra di dialogo "Aggiungi alla libreria".
 * Gestisce la selezione di una libreria da una lista tramite una ComboBox.
 */
public class AddToLibraryDialogController {

    /**
     * ComboBox che mostra la lista delle librerie disponibili.
     */
    @FXML
    private ComboBox<String> libraryComboBox;

    /**
     * Imposta la lista delle librerie disponibili nella ComboBox.
     * Se la lista non è vuota, seleziona automaticamente il primo elemento.
     *
     * @param libraries lista delle librerie da mostrare all'utente
     */
    public void setLibraries(List<String> libraries) {
        libraryComboBox.getItems().clear();
        libraryComboBox.getItems().addAll(libraries);
        if (!libraries.isEmpty()) {
            libraryComboBox.getSelectionModel().selectFirst();
        }
    }

    /**
     * Restituisce la libreria attualmente selezionata nella ComboBox.
     *
     * @return nome della libreria selezionata, oppure null se nessuna è selezionata
     */
    public String getSelectedLibrary() {
        return libraryComboBox.getValue();
    }
}
