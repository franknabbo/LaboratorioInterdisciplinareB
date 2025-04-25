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