package org.example.bookreccomender2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.kordamp.ikonli.javafx.FontIcon;
import org.example.bookreccomender2.Book;

public class AddRatingDialogController {

    @FXML private TextArea title;
    @FXML private ImageView bookCoverImage;
    @FXML private TextArea reviewText;

    // Stelle per Stile
    @FXML private FontIcon styleS1;
    @FXML private FontIcon styleS2;
    @FXML private FontIcon styleS3;
    @FXML private FontIcon styleS4;
    @FXML private FontIcon styleS5;

    // Stelle per Contenuto
    @FXML private FontIcon contentS1;
    @FXML private FontIcon contentS2;
    @FXML private FontIcon contentS3;
    @FXML private FontIcon contentS4;
    @FXML private FontIcon contentS5;

    // Stelle per Gradevolezza
    @FXML private FontIcon appealS1;
    @FXML private FontIcon appealS2;
    @FXML private FontIcon appealS3;
    @FXML private FontIcon appealS4;
    @FXML private FontIcon appealS5;

    // Stelle per Originalità
    @FXML private FontIcon originalityS1;
    @FXML private FontIcon originalityS2;
    @FXML private FontIcon originalityS3;
    @FXML private FontIcon originalityS4;
    @FXML private FontIcon originalityS5;

    // Stelle per Edizione
    @FXML private FontIcon editionS1;
    @FXML private FontIcon editionS2;
    @FXML private FontIcon editionS3;
    @FXML private FontIcon editionS4;
    @FXML private FontIcon editionS5;

    // Stelle per Voto Finale
    @FXML private FontIcon finalS1;
    @FXML private FontIcon finalS2;
    @FXML private FontIcon finalS3;
    @FXML private FontIcon finalS4;
    @FXML private FontIcon finalS5;

    private Book book;
    private int styleRating = 0;
    private int contentRating = 0;
    private int appealRating = 0;
    private int originalityRating = 0;
    private int editionRating = 0;
    private int averageRating = 0;

    @FXML
    public void initialize() {
        // Reset iniziale
        resetAllRatings();

        // Setup dei gestori di eventi
        setupStarRating(new FontIcon[]{styleS1, styleS2, styleS3, styleS4, styleS5}, this::setStyleRating, () -> styleRating);
        setupStarRating(new FontIcon[]{contentS1, contentS2, contentS3, contentS4, contentS5}, this::setContentRating, () -> contentRating);
        setupStarRating(new FontIcon[]{appealS1, appealS2, appealS3, appealS4, appealS5}, this::setAppealRating, () -> appealRating);
        setupStarRating(new FontIcon[]{originalityS1, originalityS2, originalityS3, originalityS4, originalityS5}, this::setOriginalityRating, () -> originalityRating);
        setupStarRating(new FontIcon[]{editionS1, editionS2, editionS3, editionS4, editionS5}, this::setEditionRating, () -> editionRating);

        // Configura il titolo
        if (title != null) {
            title.setStyle("-fx-pref-width: 350px;");
        }
    }

    private void setupStarRating(FontIcon[] stars, java.util.function.Consumer<Integer> ratingConsumer, java.util.function.Supplier<Integer> ratingSupplier) {
        for (int i = 0; i < stars.length; i++) {
            final int starValue = i + 1;
            FontIcon star = stars[i];

            // Evento onClick con comportamento semplificato e corretto
            star.setOnMouseClicked(event -> {
                int currentRating = ratingSupplier.get();

                // Comportamento corretto: se clicco sulla stessa stella, azzero il rating
                // altrimenti imposto il rating esattamente al valore della stella cliccata
                if (currentRating == starValue) {
                    // Se clicco sulla stella già selezionata, azzero il rating
                    ratingConsumer.accept(0);
                } else {
                    // Altrimenti imposto il rating al valore della stella cliccata
                    ratingConsumer.accept(starValue);
                }

                // Aggiorna la visualizzazione delle stelle
                updateStarRating(stars, ratingSupplier.get());
            });

            // Gestione hover invariata
            star.setOnMouseEntered(event -> {
                previewStarRating(stars, starValue);
            });

            star.setOnMouseExited(event -> {
                updateStarRating(stars, ratingSupplier.get());
            });
        }
    }

    private void previewStarRating(FontIcon[] stars, int rating) {
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].getStyleClass().add("star-selected");
                stars[i].setIconColor(javafx.scene.paint.Color.valueOf("#ffcc00"));
            } else {
                stars[i].getStyleClass().remove("star-selected");
                stars[i].setIconColor(javafx.scene.paint.Color.valueOf("#d3d3d3"));
            }
        }
    }

    public void setExistingRatings(int style, int content, int appeal, int originality, int edition) {
        // Reset completo iniziale
        resetAllRatings();

        // Imposta i valori
        this.styleRating = style;
        this.contentRating = content;
        this.appealRating = appeal;
        this.originalityRating = originality;
        this.editionRating = edition;

        //toglie la possibilità all'utente di modificare il rating
        setDisableStarIcon(styleS1, styleS2, styleS3, styleS4, styleS5, contentS1, contentS2, contentS3, contentS4, contentS5, appealS1, appealS2, appealS3, appealS4, appealS5);
        setDisableStarIcon(originalityS1, originalityS2, originalityS3, originalityS4, originalityS5, editionS1, editionS2, editionS3, editionS4, editionS5, finalS1, finalS2, finalS3, finalS4, finalS5);

        // Aggiorna la visualizzazione di tutte le stelle
        updateAllStarRatings();

        // Calcola e aggiorna la media
        updateAverageRating();
    }

    private void setDisableStarIcon(FontIcon styleS1, FontIcon styleS2, FontIcon styleS3, FontIcon styleS4, FontIcon styleS5, FontIcon contentS1, FontIcon contentS2, FontIcon contentS3, FontIcon contentS4, FontIcon contentS5, FontIcon appealS1, FontIcon appealS2, FontIcon appealS3, FontIcon appealS4, FontIcon appealS5) {
        styleS1.setDisable(true);
        styleS2.setDisable(true);
        styleS3.setDisable(true);
        styleS4.setDisable(true);
        styleS5.setDisable(true);
        contentS1.setDisable(true);
        contentS2.setDisable(true);
        contentS3.setDisable(true);
        contentS4.setDisable(true);
        contentS5.setDisable(true);
        appealS1.setDisable(true);
        appealS2.setDisable(true);
        appealS3.setDisable(true);
        appealS4.setDisable(true);
        appealS5.setDisable(true);
    }

    private void resetAllRatings() {
        // Reset di tutti i valori
        this.styleRating = 0;
        this.contentRating = 0;
        this.appealRating = 0;
        this.originalityRating = 0;
        this.editionRating = 0;

        // Reset di tutte le visualizzazioni
        updateAllStarRatings();
    }

    private void updateAllStarRatings() {
        updateStarRating(new FontIcon[]{styleS1, styleS2, styleS3, styleS4, styleS5}, styleRating);
        updateStarRating(new FontIcon[]{contentS1, contentS2, contentS3, contentS4, contentS5}, contentRating);
        updateStarRating(new FontIcon[]{appealS1, appealS2, appealS3, appealS4, appealS5}, appealRating);
        updateStarRating(new FontIcon[]{originalityS1, originalityS2, originalityS3, originalityS4, originalityS5}, originalityRating);
        updateStarRating(new FontIcon[]{editionS1, editionS2, editionS3, editionS4, editionS5}, editionRating);

        FontIcon[] finalStars = {finalS1, finalS2, finalS3, finalS4, finalS5};
        updateStarRating(finalStars, averageRating);
    }

    public void setReviewText(String text) {
        if (reviewText != null) {
            reviewText.setText(text);
        }
    }

    private void updateStarRating(FontIcon[] stars, int rating) {
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].getStyleClass().add("star-selected");
                stars[i].setIconColor(javafx.scene.paint.Color.valueOf("#ffcc00"));
            } else {
                stars[i].getStyleClass().remove("star-selected");
                stars[i].setIconColor(javafx.scene.paint.Color.valueOf("#d3d3d3"));
            }
        }
    }

    private void updateAverageRating() {
        int sum = styleRating + contentRating + appealRating + originalityRating + editionRating;
        int count = 0;
        if (styleRating > 0) count++;
        if (contentRating > 0) count++;
        if (appealRating > 0) count++;
        if (originalityRating > 0) count++;
        if (editionRating > 0) count++;

        averageRating = (count > 0) ? Math.round((float)sum / count) : 0;

        // Aggiorna le stelle del voto finale
        FontIcon[] finalStars = {finalS1, finalS2, finalS3, finalS4, finalS5};
        updateStarRating(finalStars, averageRating);
    }

    public void setBook(Book book) {
        this.book = book;
        if (book != null) {
            // Imposta il titolo del libro
            if (title != null) {
                title.setText(book.getTitle());
            }

            // Carica la copertina del libro
            if (bookCoverImage != null && book.getCoverUrl() != null) {
                try {
                    String imageUrl = book.getCoverUrl();
                    Image cover = new Image(imageUrl, true);
                    bookCoverImage.setImage(cover);
                } catch (Exception e) {
                    // In caso di errore, carica un'immagine di default
                    bookCoverImage.setImage(new Image("/logoBookRecommender.png"));
                }
            }
        }
    }

    //metodo per togliere la casella recensione
    public void setReviewTextVisible(boolean visible) {
        if (reviewText != null) {
            reviewText.setVisible(visible);
            reviewText.setManaged(visible);
        }
    }

    private void setStyleRating(int rating) {
        this.styleRating = rating;
        updateAverageRating();
    }

    private void setContentRating(int rating) {
        this.contentRating = rating;
        updateAverageRating();
    }

    private void setAppealRating(int rating) {
        this.appealRating = rating;
        updateAverageRating();
    }

    private void setOriginalityRating(int rating) {
        this.originalityRating = rating;
        updateAverageRating();
    }

    private void setEditionRating(int rating) {
        this.editionRating = rating;
        updateAverageRating();
    }

    public void setReviewTextEditable(boolean editable) {
        if (reviewText != null) {
            reviewText.setEditable(editable);
        }
    }
    public String getReviewText() {
        return reviewText.getText();
    }

    public int getStyleRating() {
        return styleRating;
    }

    public int getContentRating() {
        return contentRating;
    }

    public int getAppealRating() {
        return appealRating;
    }

    public int getOriginalityRating() {
        return originalityRating;
    }

    public int getEditionRating() {
        return editionRating;
    }

    public int getAverageRating() {
        return averageRating;
    }
}