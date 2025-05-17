// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como
package org.example.bookreccomender2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LibraryDialogController {
    @FXML
    private TextField libraryNameField;

    public String getLibraryNameField() {
        return libraryNameField.getText();
    }

}