package org.example.bookreccomender2;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LibraryDialogController {
    @FXML
    private TextField libraryNameField;

    public String getLibraryNameField() {
        return libraryNameField.getText();
    }
    public void setLibraryNameField(String libraryName) {
        if (libraryName != null) {
            this.libraryNameField.setText(libraryName);
        }
    }
}