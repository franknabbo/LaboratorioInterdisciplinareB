// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.bookrecommender2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import java.util.List;

public class AddToLibraryDialogController {
    @FXML
    private ComboBox<String> libraryComboBox;

    public void setLibraries(List<String> libraries) {
        libraryComboBox.getItems().clear();
        libraryComboBox.getItems().addAll(libraries);
        if (!libraries.isEmpty()) {
            libraryComboBox.getSelectionModel().selectFirst();
        }
    }

    public String getSelectedLibrary() {
        return libraryComboBox.getValue();
    }
}