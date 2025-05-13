package org.example.bookreccomender2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.example.bookreccomender2.*;

import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class RatingSummaryController {
    @FXML private Label mediaVotoFinaleLabel;
    @FXML private Label mediaStileLabel;
    @FXML private Label mediaContenutoLabel;
    @FXML private Label mediaGradevolezzaLabel;
    @FXML private Label mediaOriginalitaLabel;
    @FXML private Label mediaEdizioneLabel;
    @FXML private Label totalRatingsLabel;

    // Stelle per la media totale
    @FXML private FontIcon mediaTotS1;
    @FXML private FontIcon mediaTotS2;
    @FXML private FontIcon mediaTotS3;
    @FXML private FontIcon mediaTotS4;
    @FXML private FontIcon mediaTotS5;

    // Stelle per lo stile
    @FXML private FontIcon mediaStileS1;
    @FXML private FontIcon mediaStileS2;
    @FXML private FontIcon mediaStileS3;
    @FXML private FontIcon mediaStileS4;
    @FXML private FontIcon mediaStileS5;

    // Stelle per il contenuto
    @FXML private FontIcon mediaContenutoS1;
    @FXML private FontIcon mediaContenutoS2;
    @FXML private FontIcon mediaContenutoS3;
    @FXML private FontIcon mediaContenutoS4;
    @FXML private FontIcon mediaContenutoS5;

    // Stelle per la gradevolezza
    @FXML private FontIcon mediaGradevolezzaS1;
    @FXML private FontIcon mediaGradevolezzaS2;
    @FXML private FontIcon mediaGradevolezzaS3;
    @FXML private FontIcon mediaGradevolezzaS4;
    @FXML private FontIcon mediaGradevolezzaS5;

    // Stelle per l'originalità
    @FXML private FontIcon mediaOriginalitaS1;
    @FXML private FontIcon mediaOriginalitaS2;
    @FXML private FontIcon mediaOriginalitaS3;
    @FXML private FontIcon mediaOriginalitaS4;
    @FXML private FontIcon mediaOriginalitaS5;

    // Stelle per l'edizione
    @FXML private FontIcon mediaEdizioneS1;
    @FXML private FontIcon mediaEdizioneS2;
    @FXML private FontIcon mediaEdizioneS3;
    @FXML private FontIcon mediaEdizioneS4;
    @FXML private FontIcon mediaEdizioneS5;

    public void setRatingSummary(Rating mediaTotale) {
        // Imposta i valori delle label
        mediaVotoFinaleLabel.setText(String.format("%.1f", mediaTotale.getVotoFinale()));
        mediaStileLabel.setText(String.format("%.1f", mediaTotale.getStile()));
        mediaContenutoLabel.setText(String.format("%.1f", mediaTotale.getContenuto()));
        mediaGradevolezzaLabel.setText(String.format("%.1f", mediaTotale.getGradevolezza()));
        mediaOriginalitaLabel.setText(String.format("%.1f", mediaTotale.getOriginalita()));
        mediaEdizioneLabel.setText(String.format("%.1f", mediaTotale.getEdizione()));


        // Imposta le stelle per ciascuna categoria
        setStars(new FontIcon[]{mediaTotS1, mediaTotS2, mediaTotS3, mediaTotS4, mediaTotS5},
                mediaTotale.getVotoFinale());
        setStars(new FontIcon[]{mediaStileS1, mediaStileS2, mediaStileS3, mediaStileS4, mediaStileS5},
                mediaTotale.getStile());
        setStars(new FontIcon[]{mediaContenutoS1, mediaContenutoS2, mediaContenutoS3, mediaContenutoS4, mediaContenutoS5},
                mediaTotale.getContenuto());
        setStars(new FontIcon[]{mediaGradevolezzaS1, mediaGradevolezzaS2, mediaGradevolezzaS3, mediaGradevolezzaS4, mediaGradevolezzaS5},
                mediaTotale.getGradevolezza());
        setStars(new FontIcon[]{mediaOriginalitaS1, mediaOriginalitaS2, mediaOriginalitaS3, mediaOriginalitaS4, mediaOriginalitaS5},
                mediaTotale.getOriginalita());
        setStars(new FontIcon[]{mediaEdizioneS1, mediaEdizioneS2, mediaEdizioneS3, mediaEdizioneS4, mediaEdizioneS5},
                mediaTotale.getEdizione());
    }

    private void setStars(FontIcon[] stars, int rating) {
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].getStyleClass().remove("star-icon");
                stars[i].getStyleClass().add("star-selected");
            } else {
                stars[i].getStyleClass().remove("star-selected");
                stars[i].getStyleClass().add("star-icon");
            }
        }
    }
}