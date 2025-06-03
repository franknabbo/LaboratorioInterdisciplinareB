// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package bookrecommender.controller;

import bookrecommender.Book;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.kordamp.ikonli.javafx.FontIcon;

 /**
  * Controller per la finestra di dialogo che permette di aggiungere o modificare una valutazione (rating) di un libro.
  * Gestisce le stelle per le valutazioni su vari aspetti e il testo della recensione.
  */

public class AddRatingDialogController {

    /** Campo per il titolo del libro visualizzato. */
    @FXML private TextArea title;

    /** Visualizza l'immagine di copertina del libro. */
    @FXML private ImageView bookCoverImage;

    /** Area di testo per inserire o visualizzare la recensione del libro. */
    @FXML private TextArea reviewText;

    /** Stelle per la valutazione dello stile del libro. */
    @FXML private FontIcon styleS1, styleS2, styleS3, styleS4, styleS5;

    /** Stelle per la valutazione del contenuto del libro. */
    @FXML private FontIcon contentS1, contentS2, contentS3, contentS4, contentS5;

    /** Stelle per la valutazione della gradevolezza del libro. */
    @FXML private FontIcon appealS1, appealS2, appealS3, appealS4, appealS5;

    /** Stelle per la valutazione dell'originalità del libro. */
    @FXML private FontIcon originalityS1, originalityS2, originalityS3, originalityS4, originalityS5;

    /** Stelle per la valutazione dell'edizione del libro. */
    @FXML private FontIcon editionS1, editionS2, editionS3, editionS4, editionS5;

    /** Stelle per la valutazione finale (media). */
    @FXML private FontIcon finalS1, finalS2, finalS3, finalS4, finalS5;

    /** Valutazione dello stile. */
    private int styleRating = 0;

    /** Valutazione del contenuto. */
    private int contentRating = 0;

    /** Valutazione della gradevolezza (appeal). */
    private int appealRating = 0;

    /** Valutazione dell'originalità. */
    private int originalityRating = 0;

    /** Valutazione dell'edizione. */
    private int editionRating = 0;

    /** Valutazione media finale. */
    private int averageRating = 0;

    /**
     * Metodo chiamato all'inizializzazione del controller.
     * Inizializza le valutazioni e associa i gestori eventi alle stelle.
     */
    @FXML
    public void initialize() {
        resetAllRatings();

        setupStarRating(new FontIcon[]{styleS1, styleS2, styleS3, styleS4, styleS5}, this::setStyleRating, () -> styleRating);
        setupStarRating(new FontIcon[]{contentS1, contentS2, contentS3, contentS4, contentS5}, this::setContentRating, () -> contentRating);
        setupStarRating(new FontIcon[]{appealS1, appealS2, appealS3, appealS4, appealS5}, this::setAppealRating, () -> appealRating);
        setupStarRating(new FontIcon[]{originalityS1, originalityS2, originalityS3, originalityS4, originalityS5}, this::setOriginalityRating, () -> originalityRating);
        setupStarRating(new FontIcon[]{editionS1, editionS2, editionS3, editionS4, editionS5}, this::setEditionRating, () -> editionRating);

        if (title != null) {
            title.setStyle("-fx-pref-width: 350px;");
        }
    }

    /**
     * Imposta il comportamento delle stelle per una categoria di valutazione.
     * @param stars array di stelle (FontIcon)
     * @param ratingConsumer funzione per impostare il rating
     * @param ratingSupplier funzione per ottenere il rating attuale
     */
    private void setupStarRating(FontIcon[] stars, java.util.function.Consumer<Integer> ratingConsumer, java.util.function.Supplier<Integer> ratingSupplier) {
        for (int i = 0; i < stars.length; i++) {
            final int starValue = i + 1;
            FontIcon star = stars[i];

            star.setOnMouseClicked(event -> {
                int currentRating = ratingSupplier.get();

                if (currentRating == starValue) {
                    ratingConsumer.accept(0);
                } else {
                    ratingConsumer.accept(starValue);
                }

                updateStarRating(stars, ratingSupplier.get());
            });

            star.setOnMouseEntered(event -> previewStarRating(stars, starValue));

            star.setOnMouseExited(event -> updateStarRating(stars, ratingSupplier.get()));
        }
    }

    /**
     * Mostra l'anteprima delle stelle selezionate durante l'hover.
     * @param stars array di stelle
     * @param rating numero di stelle da evidenziare
     */
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

    /**
     * Imposta valutazioni esistenti e disabilita la possibilità di modificarle.
     * @param style valutazione stile
     * @param content valutazione contenuto
     * @param appeal valutazione gradevolezza
     * @param originality valutazione originalità
     * @param edition valutazione edizione
     */
    public void setExistingRatings(int style, int content, int appeal, int originality, int edition) {
        resetAllRatings();

        this.styleRating = style;
        this.contentRating = content;
        this.appealRating = appeal;
        this.originalityRating = originality;
        this.editionRating = edition;

        setDisableStarIcon(styleS1, styleS2, styleS3, styleS4, styleS5,
                contentS1, contentS2, contentS3, contentS4, contentS5,
                appealS1, appealS2, appealS3, appealS4, appealS5);

        setDisableStarIcon(originalityS1, originalityS2, originalityS3, originalityS4, originalityS5,
                editionS1, editionS2, editionS3, editionS4, editionS5,
                finalS1, finalS2, finalS3, finalS4, finalS5);

        updateAllStarRatings();
        updateAverageRating();
    }

    /**
     * Disabilita l'interazione con le stelle passate come argomento.
     * @param icons stelle da disabilitare
     */
    private void setDisableStarIcon(FontIcon... icons) {
        for (FontIcon icon : icons) {
            icon.setDisable(true);
        }
    }

    /**
     * Resetta tutte le valutazioni e aggiorna la UI.
     */
    private void resetAllRatings() {
        this.styleRating = 0;
        this.contentRating = 0;
        this.appealRating = 0;
        this.originalityRating = 0;
        this.editionRating = 0;

        updateAllStarRatings();
    }

    /**
     * Aggiorna la visualizzazione di tutte le stelle in base ai valori correnti.
     */
    private void updateAllStarRatings() {
        updateStarRating(new FontIcon[]{styleS1, styleS2, styleS3, styleS4, styleS5}, styleRating);
        updateStarRating(new FontIcon[]{contentS1, contentS2, contentS3, contentS4, contentS5}, contentRating);
        updateStarRating(new FontIcon[]{appealS1, appealS2, appealS3, appealS4, appealS5}, appealRating);
        updateStarRating(new FontIcon[]{originalityS1, originalityS2, originalityS3, originalityS4, originalityS5}, originalityRating);
        updateStarRating(new FontIcon[]{editionS1, editionS2, editionS3, editionS4, editionS5}, editionRating);

        FontIcon[] finalStars = {finalS1, finalS2, finalS3, finalS4, finalS5};
        updateStarRating(finalStars, averageRating);
    }

    /**
     * Imposta il testo della recensione.
     * @param text testo della recensione
     */
    public void setReviewText(String text) {
        if (reviewText != null) {
            reviewText.setText(text);
        }
    }

    /**
     * Aggiorna la visualizzazione delle stelle di una singola categoria in base al rating.
     * @param stars array di stelle
     * @param rating valore del rating (0-5)
     */
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

    /**
     * Calcola la media dei voti e aggiorna la visualizzazione delle stelle finali.
     */
    private void updateAverageRating() {
        int sum = styleRating + contentRating + appealRating + originalityRating + editionRating;
        int count = 0;
        if (styleRating > 0) count++;
        if (contentRating > 0) count++;
        if (appealRating > 0) count++;
        if (originalityRating > 0) count++;
        if (editionRating > 0) count++;

        averageRating = (count > 0) ? Math.round((float)sum / count) : 0;

        FontIcon[] finalStars = {finalS1, finalS2, finalS3, finalS4, finalS5};
        updateStarRating(finalStars, averageRating);
    }

    /**
     * Imposta il libro da valutare, aggiornando titolo e copertina.
     * @param book libro da impostare
     */
    public void setBook(Book book) {
        if (book != null) {
            if (title != null) {
                title.setText(book.getTitle());
            }

            if (bookCoverImage != null && book.getCoverUrl() != null) {
                try {
                    Image cover = new Image(book.getCoverUrl(), true);
                    bookCoverImage.setImage(cover);
                } catch (Exception e) {
                    bookCoverImage.setImage(new Image("/logoBookRecommender.png"));
                }
            }
        }
    }

    /**
     * Mostra o nasconde l'area di testo della recensione.
     * @param visible true per mostrare, false per nascondere
     */
    public void setReviewTextVisible(boolean visible) {
        if (reviewText != null) {
            reviewText.setVisible(visible);
            reviewText.setManaged(visible);
        }
    }

    /**
     * Imposta la valutazione dello stile.
     * @param rating valore della valutazione
     */
    private void setStyleRating(int rating) {
        this.styleRating = rating;
        updateAverageRating();
    }

    /**
     * Imposta la valutazione del contenuto.
     * @param rating valore della valutazione
     */
    private void setContentRating(int rating) {
        this.contentRating = rating;
        updateAverageRating();
    }

    /**
     * Imposta la valutazione della gradevolezza.
     * @param rating valore della valutazione
     */
    private void setAppealRating(int rating) {
        this.appealRating = rating;
        updateAverageRating();
    }

    /**
     * Imposta la valutazione dell'originalità.
     * @param rating valore della valutazione
     */
    private void setOriginalityRating(int rating) {
        this.originalityRating = rating;
        updateAverageRating();
    }

    /**
     * Imposta la valutazione dell'edizione.
     * @param rating valore della valutazione
     */
    private void setEditionRating(int rating) {
        this.editionRating = rating;
        updateAverageRating();
    }

    /**
     * Imposta la possibilità di modificare il testo della recensione.
     * @param editable true per abilitare la modifica, false per disabilitare
     */
    public void setReviewTextEditable(boolean editable) {
        if (reviewText != null) {
            reviewText.setEditable(editable);
        }
    }

    /**
     * Restituisce il testo attuale della recensione.
     * @return testo della recensione
     */
    public String getReviewText() {
        return reviewText.getText();
    }

    /** @return valutazione dello stile */
    public int getStyleRating() {
        return styleRating;
    }

    /** @return valutazione del contenuto */
    public int getContentRating() {
        return contentRating;
    }

    /** @return valutazione della gradevolezza */
    public int getAppealRating() {
        return appealRating;
    }

    /** @return valutazione dell'originalità */
    public int getOriginalityRating() {
        return originalityRating;
    }

    /** @return valutazione dell'edizione */
    public int getEditionRating() {
        return editionRating;
    }

    /** @return valutazione media finale */
    public int getAverageRating() {
        return averageRating;
    }
}
