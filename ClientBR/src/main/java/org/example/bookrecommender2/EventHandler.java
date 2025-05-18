// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example.bookrecommender2;

import javafx.scene.layout.GridPane;
import org.example.bookrecommender2.controller.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

import java.io.IOException;


import javafx.scene.control.Label;

import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class EventHandler {
    public Button addSelectedBook;
    public HBox ratingStarsContainer;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private VBox valutazioniAggregateContainer;
    @FXML
    private TextField taxCodeField;
    @FXML
    private FontIcon star1, star2, star3, star4, star5;
    @FXML
    private HBox starRatingContainer;
    @FXML
    private Label ratingValueLabel;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField userIdField;
    @FXML
    private TextField searchField;
    @FXML
    private VBox booksContainer;
    @FXML
    private Label usernameLabel;
    @FXML
    private ComboBox<String> searchTypeCombo;
    @FXML
    private TextField yearField;
    @FXML
    private Button prevPageButton;
    @FXML
    private Button nextPageButton;
    @FXML
    private Button clearSelectionButton;
    @FXML
    private Label pageLabel;
    @FXML
    private Label titoloLabel;
    @FXML
    private Label autoreLabel;
    @FXML
    private Label genereLabel;
    @FXML
    private Label editoreLabel;
    @FXML
    private Label annoLabel;
    @FXML
    private Button addRatingButton;
    @FXML
    private ImageView coverImage;
    private int currentPage = 1;
    private final int booksPerPage = 25;
    private List<Book> currentSearchResults = new ArrayList<>();
    public BookCached bookCached = BookCached.getInstance();
    private static Book selectedBook;
    @FXML
    private VBox librariesContainer;
    @FXML
    private VBox libriConsigliatiContainer;
    @FXML
    private Button valutaLibroButton;
    @FXML
    private Button consigliaLibriButton;
    @FXML
    private Button aggiungiLibreriaButton;
    @FXML
    private static List<Rating> ratingList = new ArrayList<>();

    private final AlertController alertController = new AlertController();
    private final SceneController sceneController = new SceneController();
    private final UserManager userManager = new UserManager();
    private final BookClient bookClient = new BookClient();
    private final LibraryController libraryController = new LibraryController();
    private final RatingController ratingController = new RatingController();
    private final SuggestionController suggestionController = new SuggestionController();

    private String currentLibraryName;


    @FXML
    protected void switchToRegister(ActionEvent event) {
        sceneController.switchToRegister(event);
    }

    @FXML
    protected void switchToHome(ActionEvent event) {
        sceneController.switchToHome(event);
    }

    @FXML
    protected void switchToLogin(ActionEvent event) {
        sceneController.switchToLogin(event);
    }

    @FXML
    private void switchToBookView(MouseEvent event) {
        sceneController.switchToBookView(event, selectedBook);
    }

    @FXML
    protected void switchToLibrary(ActionEvent event) {
        sceneController.switchToLibrary(event);
    }

    @FXML
    protected void switchToSelectedLibrary(MouseEvent event, String libraryName) {
        sceneController.switchToLibraryBooks(event, libraryName);
    }

    @FXML
    protected void switchToSuggestedBookList(ActionEvent event) {
        sceneController.switchToSuggestedBookList(event);
    }

    @FXML
    protected void loginUser(ActionEvent event) {
        String userId = userIdField.getText();
        String password = passwordField.getText();
        try {
            userManager.loginUser(userId, password, event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    protected void registerUser(ActionEvent event) {
        String nome = firstNameField.getText();
        String cognome = lastNameField.getText();
        String codiceFiscale = taxCodeField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        //Controllo codice fiscale
        if (codiceFiscale.length() != 16) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Codice Fiscale non valido");
            alert.setContentText("Il codice fiscale deve essere lungo 16 caratteri.");
            alert.showAndWait();
            return;
        }
        // Controllo email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Email non valida");
            alert.setContentText("Inserisci un'email valida.");
            alert.showAndWait();
            return;
        }

        // Controlla che i campi non siano vuoti
        if (nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Campi mancanti");
            alert.setContentText("Tutti i campi sono obbligatori.");
            alert.showAndWait();
            return;
        }
        userManager.registerUser(event, nome, cognome, codiceFiscale, email, password);
    }

    @FXML
    public void initialize() {
        if (usernameLabel != null && UserManager.isLoggedIn()) {
            usernameLabel.setText(UserManager.getUserId());
        }
        if (searchField != null) {
            searchField.setPromptText("Cerca un libro...");
        }

        // Configura il ComboBox per il tipo di ricerca
        if (searchTypeCombo != null) {
            searchTypeCombo.getSelectionModel().selectFirst();
            searchTypeCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                yearField.setVisible(newValue != null && newValue.equals("Per autore e anno"));
            });
        }

        // Configurazione del campo di ricerca
        setupSearchField();

        // Carica i libri solo se il container è disponibile
        if (booksContainer != null && SceneController.currentPage.contains("home") || SceneController.currentPage.contains("suggested-books")) {
            loadHomePageBooks(SceneController.currentPage);
        }


        // Carica le librerie se siamo nella vista librerie
        if (UserManager.isLoggedIn() && librariesContainer != null) {
            loadLibraries();
        }
    }

    @FXML
    private void loadLibraries() {
        new Thread(() -> {
            List<String> libraries = libraryController.getLibraryList();
            Platform.runLater(() -> showLibraryInUI(libraries));
        }).start();
    }

    @FXML
    private void addBookToLibrary(ActionEvent event) {
        try {
            // Ottieni la lista delle librerie dell'utente
            List<String> libraries = getLibraryNames();

            if (libraries.isEmpty()) {
                alertController.showAlert("Nessuna libreria", "Non hai ancora creato librerie. Creane una prima di aggiungere libri.");
                return;
            }

            // Carica il dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookrecommender2/addToLibraryDialog.fxml"));
            Parent root = loader.load();

            // Configura il controller
            AddToLibraryDialogController controller = loader.getController();
            controller.setLibraries(libraries);

            // Mostra il dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane((DialogPane) root);
            dialog.setTitle("Aggiungi a Libreria");

            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String selectedLibrary = controller.getSelectedLibrary();

                if (selectedLibrary != null && !selectedLibrary.isEmpty()) {
                    // Aggiungi il libro alla libreria
                    libraryController.addBookToSelectedLibrary(selectedLibrary, selectedBook);
                }
            }
        } catch (IOException e) {
            alertController.showAlert("Errore", "Impossibile aprire la finestra di dialogo: " + e.getMessage());
        }
    }

    //metodo per aprire la dialog per l'aggiunta della libreria
    @FXML
    private void addRating(ActionEvent event) throws IOException {
        // Carica il dialog per l'aggiunta delle recensioni
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookrecommender2/addRatingDialog.fxml"));
        Parent root = loader.load();

        // Ottieni il controller
        AddRatingDialogController controller = loader.getController();

        // Passa il libro selezionato al controller
        controller.setBook(selectedBook);

        // Crea una nuova finestra di dialogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane((DialogPane) root);
        dialog.setTitle("Aggiungi Recensione");

        // Mostra la dialog e attendi il risultato
        Optional<ButtonType> result = dialog.showAndWait();

        // Gestisci il risultato solo se l'utente ha premuto OK
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Ottieni le valutazioni e la recensione dal controller
            int styleRating = controller.getStyleRating();
            int contentRating = controller.getContentRating();
            int appealRating = controller.getAppealRating();
            int originalityRating = controller.getOriginalityRating();
            int editionRating = controller.getEditionRating();
            String reviewText = controller.getReviewText();
            int averageRating = controller.getAverageRating();

            ratingController.addRating(selectedBook.getId(), styleRating, contentRating, appealRating,
                    originalityRating, editionRating, reviewText, averageRating);
        }
    }

    @FXML
    private void openRating(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookrecommender2/addRatingDialog.fxml"));
        Parent root = loader.load();

        // Ottieni il controller
        AddRatingDialogController controller = loader.getController();

        // Passa il libro selezionato al controller
        controller.setBook(selectedBook);
        // Crea una nuova finestra di dialogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane((DialogPane) root);
        dialog.setTitle("Valutazione media");

        Rating ratingAggragato = ratingList.getFirst();

        controller.setExistingRatings(ratingAggragato.getStile(),
                ratingAggragato.getContenuto(),
                ratingAggragato.getGradevolezza(),
                ratingAggragato.getOriginalita(),
                ratingAggragato.getEdizione());

        //togli la recensione
        controller.setReviewTextVisible(false);

        // Mostra la dialog
        Optional<ButtonType> result = dialog.showAndWait();
    }

    @FXML
    private void openRating(Rating rating) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookrecommender2/addRatingDialog.fxml"));
        Parent root = loader.load();

        // Ottieni il controller
        AddRatingDialogController controller = loader.getController();
        controller.setReviewTextEditable(false);

        // Passa il libro selezionato al controller
        controller.setBook(selectedBook);
        // Crea una nuova finestra di dialogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane((DialogPane) root);
        dialog.setTitle("Recensione di: " + rating.getUserId());

        controller.setExistingRatings(rating.getStile(),
                rating.getContenuto(),
                rating.getGradevolezza(),
                rating.getOriginalita(),
                rating.getEdizione());

        //togli la recensione
        controller.setReviewTextVisible(true);
        controller.setReviewText(rating.getRecensione());

        // Mostra la dialog
        Optional<ButtonType> result = dialog.showAndWait();
    }

    private List<String> getLibraryNames() {
        List<String> libraryNames = new ArrayList<>();
        List<String> libraryEntries = libraryController.getLibraryList();

        for (String library : libraryEntries) {
            if (library.startsWith("LIBRARY:")) {
                String[] parts = library.split("\\|\\|\\|");
                if (parts.length >= 2) {
                    libraryNames.add(parts[1]); // Estrai solo il nome
                }
            }
        }

        return libraryNames;
    }

    @FXML
    private void loadHomePageBooks(String currentPage) {
        // Utilizza BookClient per ottenere i libri
        if (bookCached.hasCachedHomeBooks() && currentPage.contains("home")) {
            // Se i libri sono già stati caricati, usali dalla cache
            currentSearchResults.addAll(bookCached.getCachedHomeBooks());

            // Aggiorna controlli di paginazione
            updatePageDisplay();

            // Visualizza prima pagina
            Platform.runLater(this::displayCurrentPage);

        } else {
            BookClient client = new BookClient();
            try {
                // Richiedi libri al server
                if (currentPage.contains("home")) {
                    List<Book> books = client.getBooks(0);
                    bookCached.setCachedHomeBooks(books);
                    currentSearchResults.addAll(books);

                    // Aggiorna controlli di paginazione
                    updatePageDisplay();

                    // Visualizza prima pagina
                    Platform.runLater(this::displayCurrentPage);
                } else if (currentPage.contains("suggested")) {
                    List<Book> books = new ArrayList<>();
                    //per ogni libreria getLibraryList
                    for (String library : getLibraryNames()) {
                        books.addAll(client.getLibraryBooks(UserManager.getUserId(), library));
                    }
                    currentSearchResults.addAll(books);
                    // Aggiorna controlli di paginazione
                    updatePageDisplay();

                    // Visualizza prima pagina
                    Platform.runLater(this::displayCurrentPage);
                }

            } catch (IOException e) {
                alertController.showAlert("Errore di connessione", "Impossibile connettersi al server: " + e.getMessage());
            }
        }
    }

    private void setupSearchField() {
        if (searchField == null) return;

        searchField.setOnAction(this::handleSearch);
    }

    @FXML
    protected void handleSearch(ActionEvent event) {
        // Resetta la paginazione
        currentPage = 1;

        String searchTerm = searchField.getText().trim();
        String searchType = searchTypeCombo.getValue();


        if (searchType == null) {
            alertController.showAlert("Tipo di ricerca mancante", "Seleziona un tipo di ricerca");
            return;
        }
        // Converti il tipo di ricerca nel formato atteso dal server
        String serverSearchType;
        switch (searchType) {
            case "Per titolo":
                serverSearchType = "TITLE";
                break;
            case "Per autore":
                serverSearchType = "AUTHOR";
                break;
            case "Per autore e anno":
                serverSearchType = "AUTHOR_YEAR";
                // Verifica che l'anno sia valido
                if (yearField.getText().trim().isEmpty()) {
                    alertController.showAlert("Anno mancante", "Inserisci un anno per questo tipo di ricerca");
                    return;
                }
                break;
            default:
                alertController.showAlert("Tipo di ricerca non valido", "Seleziona un tipo di ricerca valido");
                return;
        }

        // Ottieni l'anno se necessario
        String year;
        if (serverSearchType.equals("AUTHOR_YEAR")) {
            year = yearField.getText().trim();
        } else {
            year = null;
        }


        // Esegui la ricerca in un thread separato
        new Thread(() -> {
            try {
                currentSearchResults = bookClient.performSearch(serverSearchType, searchTerm, year);
                // Aggiorna l'UI nel thread JavaFX
                Platform.runLater(() -> {
                    displayCurrentPage();
                    updatePaginationControls();
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    alertController.showAlert("Errore di connessione", "Impossibile connettersi al server: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void showUserMenu(MouseEvent event) {
        // Crea menu contestuale
        ContextMenu contextMenu = new ContextMenu();

        // Ottieni il nodo sorgente
        Node source = (Node) event.getSource();

        // Crea l'opzione per il logout
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setGraphic(new FontIcon("fas-sign-out-alt"));

        // Associa l'azione al click sull'elemento, passando il nodo sorgente
        logoutItem.setOnAction(e -> {
            UserManager.logout();

            // Ottieni lo stage direttamente qui
            Stage stage = (Stage) source.getScene().getWindow();
            try {
                String viewFile = "/org/example/bookrecommender2/homeNotLogged-view.fxml";
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(viewFile)));
                Scene scene = new Scene(root, 700, 700);
                stage.setScene(scene);
                stage.show();
            } catch (IOException _) {
            }
        });

        // Aggiungi l'opzione al menu
        contextMenu.getItems().add(logoutItem);

        // Mostra il menu nel punto del click
        contextMenu.show(source.getScene().getWindow(), event.getScreenX(), event.getScreenY());
    }

    private void showSuggestionsInUI(List<Book> books) {

        libriConsigliatiContainer.getChildren().clear();
        if (books.isEmpty()) {
            Label noSuggestionLabel = new Label("Nessun suggertimento disponibile per questo libro.");
            noSuggestionLabel.getStyleClass().add("no-reviews-label");
            libriConsigliatiContainer.getChildren().add(noSuggestionLabel);
            return;
        }
        for (Book book : books) {
            addBookToUI(book);
        }
    }


    public void updateBookRating(HBox ratingStarsContainer) {
        // Ottieni la valutazione media
        if (ratingList.isEmpty()) {
            for (int i = 0; i < 5; i++) {
                FontIcon star = (FontIcon) ratingStarsContainer.getChildren().get(i);
                star.setIconColor(javafx.scene.paint.Color.valueOf("#d3d3d3"));
            }
        } else {
            Rating rating = ratingList.getFirst(); // Ottieni la prima valutazione

            if (rating != null) {
                int ratingValue = rating.getVotoFinale();
                for (int i = 0; i < 5; i++) {
                    FontIcon star = (FontIcon) ratingStarsContainer.getChildren().get(i);
                    if (i < ratingValue) {
                        star.setIconColor(javafx.scene.paint.Color.valueOf("#ffcc00"));
                    } else {
                        star.setIconColor(javafx.scene.paint.Color.valueOf("#d3d3d3"));
                    }
                }
            }
        }
    }


    public void showRating() {

        loadAndDisplayBookReviews(selectedBook.getId());

    }

    public void initBookData(Book book) {
        if (book == null) return;
        SceneController.currentPage = "book-view.fxml";
        // Memorizza il libro selezionato
        selectedBook = book;
        ratingList = ratingController.fetchRating(selectedBook.getId());
        List<Book> suggestedBookList = suggestionController.getSuggestedBooks(selectedBook.getId());

        updateBookRating(ratingStarsContainer);
        showSuggestionsInUI(suggestedBookList);


        // Aggiorna i campi della vista con i dati del libro
        if (titoloLabel != null) titoloLabel.setText(book.getTitle());
        if (autoreLabel != null) autoreLabel.setText(book.getAuthor());
        if (genereLabel != null) genereLabel.setText(book.getCategory());
        if (editoreLabel != null) editoreLabel.setText(book.getPublisher());
        if (annoLabel != null) annoLabel.setText(book.getPublicationYear());

        // Gestisci la visibilità delle sezioni in base allo stato di login
        if (valutaLibroButton != null) {
            valutaLibroButton.setVisible(UserManager.isLoggedIn());
            valutaLibroButton.setManaged(UserManager.isLoggedIn());
        }

        if (consigliaLibriButton != null) {
            consigliaLibriButton.setVisible(UserManager.isLoggedIn());
            consigliaLibriButton.setManaged(UserManager.isLoggedIn());
        }
        if (aggiungiLibreriaButton != null) {
            aggiungiLibreriaButton.setVisible(UserManager.isLoggedIn());
            aggiungiLibreriaButton.setManaged(UserManager.isLoggedIn());
        }


        // Carica la copertina
        if (coverImage != null) {
            if (book.getCoverUrl() != null && !book.getCoverUrl().equals("null")) {
                try {
                    // Estrai l'URL effettivo
                    String imageUrl = book.getCoverUrl();
                    // Carica l'immagine
                    Image cover = new Image(imageUrl, true);
                    coverImage.setImage(cover);
                } catch (Exception e) {
                    coverImage.setImage(new Image("/logoBookRecommender.png"));
                }
            } else {
                coverImage.setImage(new Image("/logoBookRecommender.png"));
            }
        }
    }

    private void loadAndDisplayBookReviews(int bookId) {
        valutazioniAggregateContainer.getChildren().clear();

        // Ottieni le recensioni dal database tramite il controller
        List<Rating> ratings = ratingList;

        if (ratings.isEmpty()) {
            Label noReviewsLabel = new Label("Nessuna recensione disponibile per questo libro.");
            noReviewsLabel.getStyleClass().add("no-reviews-label");
            valutazioniAggregateContainer.getChildren().add(noReviewsLabel);
            return;
        }

        // La prima valutazione è la media totale, non mostrare come recensione
        for (int i = 1; i < ratings.size(); i++) {
            Rating rating = ratings.get(i);
            VBox reviewBox = createReviewBox(rating);

            reviewBox.setOnMouseClicked(event -> {
                try {
                    openRating(rating);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            // Aggiungi stile per indicare che è cliccabile
            reviewBox.setCursor(Cursor.HAND);
            reviewBox.setOnMouseEntered(e -> reviewBox.setStyle(reviewBox.getStyle() + "-fx-border-color: #8B4513;"));
            reviewBox.setOnMouseExited(e -> reviewBox.setStyle(reviewBox.getStyle() + "-fx-border-color: #ddd;"));


            valutazioniAggregateContainer.getChildren().add(reviewBox);
        }
    }

    private VBox createReviewBox(Rating rating) {
        VBox reviewBox = new VBox(10);
        reviewBox.getStyleClass().add("review-box");

        // Intestazione con ID utente e valutazione
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label userLabel = new Label(rating.getUserId() + ":");
        userLabel.getStyleClass().add("review-user");

        HBox starsBox = createRatingStars(rating.getVotoFinale());

        header.getChildren().addAll(userLabel, starsBox);

        // Dettagli della valutazione
        GridPane ratingDetails = new GridPane();
        ratingDetails.setHgap(10);
        ratingDetails.setVgap(5);

        reviewBox.getChildren().addAll(header, ratingDetails);

        return reviewBox;
    }

    private HBox createRatingStars(int rating) {
        HBox starsBox = new HBox(5);
        starsBox.setAlignment(Pos.CENTER_LEFT);

        for (int i = 1; i <= 5; i++) {
            FontIcon star = new FontIcon();
            star.setIconLiteral("fas-star");
            star.setIconSize(16);

            if (i <= rating) {
                star.setIconColor(Color.GOLD);
            } else {
                star.setIconColor(Color.GRAY);
            }

            starsBox.getChildren().add(star);
        }

        return starsBox;
    }


    private void addBookToUI(Book book) {
        if (SceneController.currentPage.contains("suggested-books")) {
            if (book.getId() == selectedBook.getId()) {
                return;
            }
            // Crea l'elemento visuale del libro
            HBox bookItem = new HBox();
            bookItem.getStyleClass().add("book-item");
            bookItem.setSpacing(15.0);

            // Copertina
            ImageView coverView = new ImageView();
            coverView.setFitWidth(120.0);
            coverView.setFitHeight(180.0);
            coverView.setPreserveRatio(true);

            // Carica l'immagine di copertina
            if (!book.getCoverUrl().equals("null")) {
                try {
                    String imageUrl = book.getCoverUrl();
                    // Caricamento immagine
                    Image coverImage = new Image(imageUrl, true);
                    // Listener per errori
                    coverImage.errorProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            Platform.runLater(() -> coverView.setImage(new Image("/logoBookRecommender.png")));
                        }
                    });

                    coverView.setImage(coverImage);

                } catch (Exception e) {
                    coverView.setImage(new Image("/logoBookRecommender.png"));
                }
            } else {
                coverView.setImage(new Image("/logoBookRecommender.png"));
            }

            // Contenitore per i dettagli testuali
            VBox contentBox = new VBox();
            contentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            contentBox.setSpacing(10.0);

            // Titolo del libro
            Label titleLabel = new Label(book.getTitle());
            titleLabel.getStyleClass().add("book-title");

            // Autore del libro
            Label authorLabel = new Label("Autore: " + (book.getAuthor().length() >= 3 ? book.getAuthor().substring(3) : book.getAuthor()));
            authorLabel.getStyleClass().add("book-author");
            // Categoria del libro
            Label categoryLabel = new Label("Categoria: " + book.getCategory());
            categoryLabel.getStyleClass().add("book-category");
            // Editore del libro
            Label publisherLabel = new Label("Editore: " + book.getPublisher());
            publisherLabel.getStyleClass().add("book-publisher");
            // Anno di pubblicazione del libro
            Label yearLabel = new Label("Anno: " + book.getPublicationYear());
            yearLabel.getStyleClass().add("book-year");
            //aggiunge la checkbox per ogni libro
            CheckBox checkBox = new CheckBox();
            checkBox.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
            checkBox.setSelected(false);

            checkBox.setOnAction(e -> {
                updateAddSuggestionsButtonState();
            });
            // Aggiungi la checkbox al contentBox
            contentBox.getChildren().add(checkBox);
            // Aggiungi gli elementi testuali al contentBox
            contentBox.getChildren().addAll(titleLabel, authorLabel, categoryLabel, publisherLabel, yearLabel);
            // Aggiungi copertina e contenitore di testo all'elemento libro
            bookItem.getChildren().addAll(coverView, contentBox);
            //make the bookItem not clickable
            bookItem.setCursor(Cursor.DEFAULT);
            // Aggiungi l'elemento libro al container
            booksContainer.getChildren().add(bookItem);
        } else if (SceneController.currentPage.contains("home") || SceneController.currentPage.contains("library-books")) {
            // Crea l'elemento visuale del libro
            HBox bookItem = new HBox();
            bookItem.getStyleClass().add("book-item");
            bookItem.setSpacing(15.0);

            // Copertina
            ImageView coverView = new ImageView();
            coverView.setFitWidth(120.0);
            coverView.setFitHeight(180.0);
            coverView.setPreserveRatio(true);

            // Carica l'immagine di copertina
            if (!book.getCoverUrl().equals("null")) {
                try {

                    // Caricamento immagine
                    Image coverImage = new Image(book.getCoverUrl(), true);

                    // Listener per errori
                    coverImage.errorProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            Platform.runLater(() -> coverView.setImage(new Image("/logoBookRecommender.png")));
                        }
                    });

                    coverView.setImage(coverImage);

                } catch (Exception e) {
                    coverView.setImage(new Image("/logoBookRecommender.png"));
                }
            } else {
                coverView.setImage(new Image("/logoBookRecommender.png"));
            }

            // Contenitore per i dettagli testuali
            VBox contentBox = new VBox();
            contentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            contentBox.setSpacing(10.0);

            // Titolo del libro
            Label titleLabel = new Label(book.getTitle());
            titleLabel.getStyleClass().add("book-title");

            // Autore del libro
            Label authorLabel = new Label("Autore: " + (book.getAuthor().length() >= 3 ? book.getAuthor().substring(3) : book.getAuthor()));
            authorLabel.getStyleClass().add("book-author");

            // Categoria del libro
            Label categoryLabel = new Label("Categoria: " + book.getCategory());
            categoryLabel.getStyleClass().add("book-category");

            // Editore del libro
            Label publisherLabel = new Label("Editore: " + book.getPublisher());
            publisherLabel.getStyleClass().add("book-publisher");

            // Anno di pubblicazione del libro
            Label yearLabel = new Label("Anno: " + book.getPublicationYear());
            yearLabel.getStyleClass().add("book-year");

            // Aggiungi gli elementi testuali al contentBox
            contentBox.getChildren().addAll(titleLabel, authorLabel, categoryLabel, publisherLabel, yearLabel);

            // Aggiungi copertina e contenitore di testo all'elemento libro
            bookItem.getChildren().addAll(coverView, contentBox);


            bookItem.setCursor(Cursor.HAND);
            bookItem.setOnMouseClicked(event -> {
                selectedBook = book;
                switchToBookView(event);
            });

            booksContainer.getChildren().add(bookItem);

        } else if (SceneController.currentPage.contains("book-view.fxml")) {
            // Crea l'elemento visivo del libro
            HBox bookItem = new HBox();
            bookItem.getStyleClass().add("book-item");
            bookItem.setSpacing(15.0);

            // Copertina
            ImageView coverView = new ImageView();
            coverView.setFitWidth(120.0);
            coverView.setFitHeight(180.0);
            coverView.setPreserveRatio(true);

            // Carica l'immagine di copertina
            if (!book.getCoverUrl().equals("null")) {
                try {
                    String imageUrl = book.getCoverUrl();
                    // Caricamento immagine
                    Image coverImage = new Image(imageUrl, true);
                    // Listener per errori
                    coverImage.errorProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            Platform.runLater(() -> coverView.setImage(new Image("/logoBookRecommender.png")));
                        }
                    });

                    coverView.setImage(coverImage);

                } catch (Exception e) {
                    coverView.setImage(new Image("/logoBookRecommender.png"));
                }
            } else {
                coverView.setImage(new Image("/logoBookRecommender.png"));
            }

            // Contenitore per i dettagli testuali
            VBox contentBox = new VBox();
            contentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            contentBox.setSpacing(10.0);

            // Titolo del libro
            Label titleLabel = new Label(book.getTitle());
            titleLabel.getStyleClass().add("book-title");

            // Autore del libro
            Label authorLabel = new Label("Autore: " + (book.getAuthor().length() >= 3 ? book.getAuthor().substring(3) : book.getAuthor()));
            authorLabel.getStyleClass().add("book-author");

            // Categoria del libro
            Label categoryLabel = new Label("Categoria: " + book.getCategory());
            categoryLabel.getStyleClass().add("book-category");

            // Editore del libro
            Label publisherLabel = new Label("Editore: " + book.getPublisher());
            publisherLabel.getStyleClass().add("book-publisher");

            // Anno di pubblicazione del libro
            Label yearLabel = new Label("Anno: " + book.getPublicationYear());
            yearLabel.getStyleClass().add("book-year");
            // Aggiungi gli elementi testuali al contentBox
            contentBox.getChildren().addAll(titleLabel, authorLabel, categoryLabel, publisherLabel, yearLabel);
            // Aggiungi copertina e contenitore di testo all'elemento libro
            bookItem.getChildren().addAll(coverView, contentBox);
            bookItem.setCursor(Cursor.HAND);
            bookItem.setOnMouseClicked(event -> {
                selectedBook = book;
                switchToBookView(event);
            });
            // Aggiungi l'elemento libro al container
            libriConsigliatiContainer.getChildren().add(bookItem);

        }
    }

    @FXML
    protected void goToNextPage(ActionEvent event) {
        if (currentPage < getTotalPages()) {
            currentPage++;
            updatePaginationControls();
            displayCurrentPage();
        }
    }

    @FXML
    protected void goToPrevPage(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            updatePaginationControls();
            displayCurrentPage();
        }
    }

    private int getTotalPages() {
        return (int) Math.ceil((double) currentSearchResults.size() / booksPerPage);
    }

    private void updatePaginationControls() {
        int totalPages = getTotalPages();

        // Verifica che pageLabel esista prima di usarlo
        if (pageLabel != null) {
            pageLabel.setText("Pagina " + currentPage + " di " + totalPages);
        }

        // Verifica anche gli altri controlli
        if (nextPageButton != null) {
            nextPageButton.setDisable(currentPage >= totalPages);
        }

        if (prevPageButton != null) {
            prevPageButton.setDisable(currentPage <= 1);
        }
    }

    private void updatePageDisplay() {
        if (pageLabel != null) {
            pageLabel.setText("Pagina " + currentPage);
        }

        // Aggiorna stato dei pulsanti se esistono
        if (prevPageButton != null) {
            prevPageButton.setDisable(currentPage <= 1);
        }

        int totalPages = getTotalPages();
        if (nextPageButton != null) {
            nextPageButton.setDisable(currentPage >= totalPages);
        }

        if (pageLabel != null || nextPageButton != null || prevPageButton != null) {
            updatePaginationControls();
        }
    }

    private void displayCurrentPage() {
        if (booksContainer == null) return;

        // Pulisci il container
        booksContainer.getChildren().clear();

        // Calcola gli indici per la pagina corrente
        int startIndex = (currentPage - 1) * booksPerPage;
        int endIndex = Math.min(startIndex + booksPerPage, currentSearchResults.size());

        // Mostra i libri per la pagina corrente
        for (int i = startIndex; i < endIndex; i++) {
            Book book = currentSearchResults.get(i);
            addBookToUI(book);
        }
    }

    private void showLibraryInUI(List<String> libraryNames) {
        // Verifica che librariesContainer non sia null
        if (librariesContainer == null) {
            System.err.println("Errore: librariesContainer è null");
            return;
        }

        // Aggiungi le librerie al container
        librariesContainer.getChildren().clear();
        for (String library : libraryNames) {
            if (library.startsWith("LIBRARY:")) {
                String[] parts = library.split("\\|\\|\\|");
                if (parts.length >= 3) {
                    // Crea un pulsante per ogni libreria
                    Button libraryButton = new Button();
                    libraryButton.getStyleClass().add("library-button");
                    libraryButton.setMaxWidth(Double.MAX_VALUE);
                    libraryButton.setStyle("-fx-background-color: #e6d7c3; -fx-background-radius: 8;");

                    // Imposta il padding
                    libraryButton.setPadding(new Insets(15.0, 20.0, 15.0, 20.0));

                    // Crea il contenuto grafico
                    HBox contentBox = new HBox();
                    contentBox.setAlignment(Pos.CENTER_LEFT);
                    contentBox.setSpacing(15);
                    HBox.setHgrow(contentBox, Priority.ALWAYS);

                    // Aggiungi l'immagine della libreria
                    ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/LibraryIcon.png"))));
                    imageView.setFitWidth(80);
                    imageView.setFitHeight(80);
                    imageView.setPreserveRatio(true);

                    // Modifica la parte relativa all'etichetta del nome
                    Label nameLabel = new Label(parts[1]); // Il nome è la seconda parte
                    nameLabel.getStyleClass().add("library-title");
                    // Marrone scuro caldo
                    nameLabel.setFont(Font.font("System", FontWeight.BOLD, 24.0)); // Aumentato la dimensione del font
                    nameLabel.setTextFill(Color.valueOf("#4A3C32"));
                    nameLabel.setStyle(nameLabel.getStyle() + "-fx-text-fill: #4A3C32;");
// Aggiungere effetto ombra al testo
                    DropShadow dropShadow = new DropShadow();
                    dropShadow.setRadius(2.0);
                    dropShadow.setOffsetX(1.0);
                    dropShadow.setOffsetY(1.0);
                    dropShadow.setColor(Color.rgb(0, 0, 0, 0.3));
                    nameLabel.setEffect(dropShadow);

// Modificare lo stile del pulsante per un migliore contrasto
                    libraryButton.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0e6d8, #e6d7c3);" +
                            "-fx-background-radius: 12;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

                    // Assembla il contenuto
                    contentBox.getChildren().addAll(imageView, nameLabel);
                    libraryButton.setGraphic(contentBox);

                    // Aggiungi l'evento di click per aprire la libreria
                    final String libraryName = parts[1]; // Cattura il nome della libreria
                    libraryButton.setOnMouseClicked(event -> switchToSelectedLibrary(event, libraryName));

                    // Aggiungi il pulsante al container
                    librariesContainer.getChildren().add(libraryButton);
                }
            }
        }

        // Se non ci sono librerie, mostra un messaggio
        if (librariesContainer.getChildren().isEmpty()) {
            Label emptyLabel = new Label("Non hai ancora creato librerie");
            emptyLabel.getStyleClass().add("empty-message");
            librariesContainer.getChildren().add(emptyLabel);
        }
    }

    @FXML
    private void createNewLibrary(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookrecommender2/libraryDialog.fxml"));
            Parent root = loader.load();

            // Ottieni il controller della dialog
            LibraryDialogController controller = loader.getController();

            // Crea una nuova finestra di dialogo
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Crea Nuova Libreria");
            dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());

            // Usa la Dialog API per gestire i pulsanti OK e Cancel
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane((DialogPane) root);
            dialog.setTitle("Crea Nuova Libreria");

            // Mostra la dialog e attendi il risultato
            Optional<ButtonType> result = dialog.showAndWait();

            // Gestisci il risultato solo se l'utente ha premuto OK
            if (result.isPresent() && result.get() == ButtonType.OK) {
                String libraryName = controller.getLibraryNameField();

                // Verifica che il nome non sia vuoto
                if (libraryName != null && !libraryName.trim().isEmpty() && !libraryName.contains(":")) {
                    // Procedi con la creazione della libreria
                    if (libraryController.createLibraryWithName(libraryName)) {
                        loadLibraries(); // Ricarica le librerie
                    }
                } else {
                    alertController.showAlert("Nome libreria non valido", "Inserisci un nome valido per la libreria.");
                }
            }
        } catch (IOException e) {
            alertController.showAlert("Errore", "Impossibile aprire la finestra di dialogo: " + e.getMessage());
        }
    }

    public void initLibraryBooksView(String libraryName) {
        // Salva il nome della libreria corrente
        this.currentLibraryName = libraryName;

        // Carica i libri della libreria
        loadLibraryBooks(currentLibraryName);
    }

    private void loadLibraryBooks(String libraryName) {
        new Thread(() -> {
            try {
                List<Book> books = bookClient.getLibraryBooks(UserManager.getUserId(), libraryName);
                booksContainer.getChildren().clear();
                Platform.runLater(() -> {
                    if (booksContainer != null) {
                        if (books.isEmpty()) {
                            Label emptyLabel = new Label("Nessun libro in questa libreria");
                            emptyLabel.getStyleClass().add("empty-message");
                            booksContainer.getChildren().add(emptyLabel);
                        } else {
                            // Aggiungi i libri all'interfaccia
                            for (Book book : books) {
                                try {
                                    addBookToUI(book);
                                } catch (Exception e) {
                                    System.err.println("Errore visualizzazione libro: " + e.getMessage());
                                }
                            }
                        }
                    } else {
                        System.err.println("booksContainer è null");
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    System.err.println("Errore di connessione: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void addSuggestions(ActionEvent event) {
        List<Book> selectedBooks = new ArrayList<>();

        // Scansiona tutti i container dei libri per trovare le checkbox selezionate
        for (Node node : booksContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox bookItem = (HBox) node;
                // Cerca nel contenuto del libro
                for (Node child : bookItem.getChildren()) {
                    if (child instanceof VBox) {
                        VBox contentBox = (VBox) child;
                        // Cerca la checkbox
                        for (Node contentChild : contentBox.getChildren()) {
                            if (contentChild instanceof CheckBox) {
                                CheckBox checkBox = (CheckBox) contentChild;
                                if (checkBox.isSelected()) {
                                    // Trova l'indice dell'elemento nel container
                                    int index = booksContainer.getChildren().indexOf(bookItem);
                                    // Calcola l'indice reale nella lista dei risultati
                                    int realIndex = (currentPage - 1) * booksPerPage + index;

                                    if (realIndex < currentSearchResults.size()) {
                                        selectedBooks.add(currentSearchResults.get(realIndex));
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }


        // Verifica se ci sono libri selezionati
        if (selectedBooks.isEmpty()) {
            alertController.showAlert("Nessun libro selezionato", "Seleziona almeno un libro da suggerire.");
            return;
        }

        if (suggestionController.addSuggestedBook(UserManager.getUserId(), selectedBook.getId(), selectedBooks)) {
            alertController.showAlertSucces("Suggerimenti aggiunti", "I libri selezionati sono stati suggeriti con successo.");
            //torna nella schermata principale
            try {
                switchToHome(event);
            } catch (Exception e) {
                alertController.showAlert("Errore", "Impossibile tornare alla schermata principale.");
            }
        } else {
            alertController.showAlert("Suggerimenti non aggiunti", "I libri selezionati non sono stati suggeriti con successo, assicurati di avere il libro in una libreria personale");

        }
    }
    /**
     * Aggiorna lo stato del pulsante "Aggiungi Suggerimenti" in base al numero di libri selezionati
     */
    private void updateAddSuggestionsButtonState() {
        int selectedCount = 0;

        // Conta i libri selezionati
        for (Node node : booksContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox bookItem = (HBox) node;
                for (Node child : bookItem.getChildren()) {
                    if (child instanceof VBox) {
                        VBox contentBox = (VBox) child;
                        for (Node contentChild : contentBox.getChildren()) {
                            if (contentChild instanceof CheckBox) {
                                CheckBox checkBox = (CheckBox) contentChild;
                                if (checkBox.isSelected()) {
                                    selectedCount++;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        // Abilita il pulsante solo se sono selezionati tra 1 e 3 libri
        addSelectedBook.setDisable(selectedCount < 1 || selectedCount > 3);
    }

    @FXML
    private void clearBookSelection(ActionEvent event) {
        // Deseleziona tutte le checkbox
        for (Node node : booksContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox bookItem = (HBox) node;
                for (Node child : bookItem.getChildren()) {
                    if (child instanceof VBox) {
                        VBox contentBox = (VBox) child;
                        for (Node contentChild : contentBox.getChildren()) {
                            if (contentChild instanceof CheckBox) {
                                CheckBox checkBox = (CheckBox) contentChild;
                                checkBox.setSelected(false);
                            }
                        }
                    }
                }
            }
        }
        updateAddSuggestionsButtonState();
        try {
            switchToHome(event);
        } catch (Exception e) {
            alertController.showAlert("Errore", "Impossibile tornare alla schermata precedente.");
        }
    }


}