package org.example.bookreccomender2;

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