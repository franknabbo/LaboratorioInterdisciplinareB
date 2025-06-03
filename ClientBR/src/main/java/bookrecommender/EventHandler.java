// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package bookrecommender;

import javafx.scene.layout.GridPane;
import bookrecommender.controller.*;
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

/**
 * Classe che gestisce gli eventi dell'interfaccia grafica,
 * collegata ai controlli FXML e responsabile della navigazione tra scene,
 * gestione utenti, libri, librerie e valutazioni.
 */
public class EventHandler {

    /**
     * Bottone per aggiungere il libro selezionato.
     */
    public Button addSelectedBook;

    /**
     * Contenitore orizzontale per le stelle di valutazione.
     */
    public HBox ratingStarsContainer;

    /**
     * Campo di testo per il nome dell'utente.
     */
    @FXML
    private TextField firstNameField;

    /**
     * Campo di testo per il cognome dell'utente.
     */
    @FXML
    private TextField lastNameField;

    /**
     * Contenitore verticale per mostrare valutazioni aggregate.
     */
    @FXML
    private VBox valutazioniAggregateContainer;

    /**
     * Campo di testo per il codice fiscale dell'utente.
     */
    @FXML
    private TextField taxCodeField;

    /**
     * Icona stella 1 per la valutazione.
     */
    @FXML
    private FontIcon star1;

    /**
     * Icona stella 2 per la valutazione.
     */
    @FXML
    private FontIcon star2;

    /**
     * Icona stella 3 per la valutazione.
     */
    @FXML
    private FontIcon star3;

    /**
     * Icona stella 4 per la valutazione.
     */
    @FXML
    private FontIcon star4;

    /**
     * Icona stella 5 per la valutazione.
     */
    @FXML
    private FontIcon star5;

    /**
     * Contenitore orizzontale per la valutazione a stelle.
     */
    @FXML
    private HBox starRatingContainer;

    /**
     * Etichetta che mostra il valore numerico della valutazione.
     */
    @FXML
    private Label ratingValueLabel;

    /**
     * Campo di testo per l'email dell'utente.
     */
    @FXML
    private TextField emailField;

    /**
     * Campo password per la password dell'utente.
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Campo di testo per l'ID utente (username).
     */
    @FXML
    private TextField userIdField;

    /**
     * Campo di testo per la ricerca libri.
     */
    @FXML
    private TextField searchField;

    /**
     * Contenitore verticale per mostrare la lista dei libri.
     */
    @FXML
    private VBox booksContainer;

    /**
     * Etichetta che mostra il nome utente attualmente loggato.
     */
    @FXML
    private Label usernameLabel;

    /**
     * ComboBox per selezionare il tipo di ricerca.
     */
    @FXML
    private ComboBox<String> searchTypeCombo;

    /**
     * Campo di testo per inserire l'anno (visibile solo per alcune ricerche).
     */
    @FXML
    private TextField yearField;

    /**
     * Bottone per andare alla pagina precedente della lista libri.
     */
    @FXML
    private Button prevPageButton;

    /**
     * Bottone per andare alla pagina successiva della lista libri.
     */
    @FXML
    private Button nextPageButton;

    /**
     * Bottone per cancellare la selezione corrente.
     */
    @FXML
    private Button clearSelectionButton;

    /**
     * Etichetta che mostra il numero della pagina corrente.
     */
    @FXML
    private Label pageLabel;

    /**
     * Etichetta che mostra il titolo del libro selezionato.
     */
    @FXML
    private Label titoloLabel;

    /**
     * Etichetta che mostra l'autore del libro selezionato.
     */
    @FXML
    private Label autoreLabel;

    /**
     * Etichetta che mostra il genere del libro selezionato.
     */
    @FXML
    private Label genereLabel;

    /**
     * Etichetta che mostra l'editore del libro selezionato.
     */
    @FXML
    private Label editoreLabel;

    /**
     * Etichetta che mostra l'anno di pubblicazione del libro selezionato.
     */
    @FXML
    private Label annoLabel;

    /**
     * Bottone per aggiungere una valutazione a un libro.
     */
    @FXML
    private Button addRatingButton;

    /**
     * Immagine di copertina del libro selezionato.
     */
    @FXML
    private ImageView coverImage;

    /**
     * Contatore della pagina corrente per la paginazione.
     */
    private int currentPage = 1;

    /**
     * Numero di libri mostrati per pagina.
     */
    private final int booksPerPage = 25;

    /**
     * Lista dei libri risultanti dalla ricerca corrente.
     */
    private List<Book> currentSearchResults = new ArrayList<>();

    /**
     * Istanza singleton per la cache dei libri.
     */
    public BookCached bookCached = BookCached.getInstance();

    /**
     * Dati del libro attualmente selezionato.
     */
    private static Book selectedBookData;

    /**
     * Contenitore verticale per mostrare le librerie.
     */
    @FXML
    private VBox librariesContainer;

    /**
     * Contenitore verticale per mostrare i libri consigliati.
     */
    @FXML
    private VBox libriConsigliatiContainer;

    /**
     * Bottone per valutare un libro.
     */
    @FXML
    private Button valutaLibroButton;

    /**
     * Bottone per chiedere consigli su libri.
     */
    @FXML
    private Button consigliaLibriButton;

    /**
     * Bottone per aggiungere una nuova libreria.
     */
    @FXML
    private Button aggiungiLibreriaButton;

    /**
     * Lista statica delle valutazioni.
     */
    private static List<Rating> ratingList = new ArrayList<>();

    /**
     * Controller per la gestione degli alert.
     */
    private final AlertController alertController = new AlertController();

    /**
     * Controller per il cambio di scena.
     */
    private final SceneController sceneController = new SceneController();

    /**
     * Manager per la gestione degli utenti.
     */
    private final UserManager userManager = new UserManager();

    /**
     * Client per la gestione delle chiamate legate ai libri.
     */
    private final BookClient bookClient = new BookClient();

    /**
     * Controller per la gestione delle librerie.
     */
    private final LibraryController libraryController = new LibraryController();

    /**
     * Controller per la gestione delle valutazioni.
     */
    private final RatingController ratingController = new RatingController();

    /**
     * Controller per la gestione dei suggerimenti di libri.
     */
    private final SuggestionController suggestionController = new SuggestionController();

    /**
     * Nome della libreria attualmente selezionata.
     */
    private String currentLibraryName;

    /**
     * Cambia la scena alla schermata di registrazione utente.
     *
     * @param event evento di azione (es. click su bottone)
     */
    @FXML
    protected void switchToRegister(ActionEvent event) {
        sceneController.switchToRegister(event);
    }

    /**
     * Cambia la scena alla schermata principale (home).
     *
     * @param event evento di azione
     */
    @FXML
    protected void switchToHome(ActionEvent event) {
        sceneController.switchToHome(event);
    }

    /**
     * Cambia la scena alla schermata di login.
     *
     * @param event evento di azione
     */
    @FXML
    protected void switchToLogin(ActionEvent event) {
        sceneController.switchToLogin(event);
    }

    /**
     * Cambia la scena alla visualizzazione dettagliata del libro selezionato.
     *
     * @param event evento di input del mouse
     */
    @FXML
    private void switchToBookView(MouseEvent event) {
        sceneController.switchToBookView(event, selectedBookData);
    }

    /**
     * Cambia la scena alla visualizzazione librerie.
     *
     * @param event evento di azione
     */
    @FXML
    protected void switchToLibrary(ActionEvent event) {
        sceneController.switchToLibrary(event);
    }

    /**
     * Cambia la scena alla visualizzazione dei libri di una libreria specifica.
     *
     * @param event       evento di input mouse
     * @param libraryName nome della libreria selezionata
     */
    @FXML
    protected void switchToSelectedLibrary(MouseEvent event, String libraryName) {
        sceneController.switchToLibraryBooks(event, libraryName);
    }

    /**
     * Cambia la scena alla lista di libri consigliati.
     *
     * @param event evento di azione
     */
    @FXML
    protected void switchToSuggestedBookList(ActionEvent event) {
        sceneController.switchToSuggestedBookList(event);
    }

    /**
     * Esegue il login dell'utente usando i dati inseriti nei campi.
     *
     * @param event evento di azione
     */
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

    /**
     * Esegue la registrazione di un nuovo utente validando i dati immessi.
     * Mostra eventuali errori all'utente tramite Alert.
     *
     * @param event evento di azione
     */
    @FXML
    protected void registerUser(ActionEvent event) {
        String nome = firstNameField.getText();
        String cognome = lastNameField.getText();
        String codiceFiscale = taxCodeField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (codiceFiscale.length() != 16) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Codice Fiscale non valido");
            alert.setContentText("Il codice fiscale deve essere lungo 16 caratteri.");
            alert.showAndWait();
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Email non valida");
            alert.setContentText("Inserisci un'email valida.");
            alert.showAndWait();
            return;
        }
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

    /**
     * Metodo di inizializzazione chiamato automaticamente da JavaFX.
     * Configura campi di testo, ComboBox e carica i dati iniziali necessari.
     */
    @FXML
    public void initialize() {
        if (usernameLabel != null && UserManager.isLoggedIn()) {
            usernameLabel.setText(UserManager.getUserId());
        }
        if (searchField != null) {
            searchField.setPromptText("Cerca un libro...");
        }

        if (searchTypeCombo != null) {
            searchTypeCombo.getSelectionModel().selectFirst();
            searchTypeCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                yearField.setVisible(newValue != null && newValue.equals("Per autore e anno"));
            });
        }

        setupSearchField();

        if (booksContainer != null && (SceneController.currentPage.contains("home") || SceneController.currentPage.contains("suggested-books"))) {
            loadHomePageBooks(SceneController.currentPage);
        }

        if (UserManager.isLoggedIn() && librariesContainer != null) {
            loadLibraries();
        }
    }

    /**
     * Carica la lista delle librerie dell'utente in un thread separato e aggiorna la UI.
     */
    @FXML
    private void loadLibraries() {
        new Thread(() -> {
            List<String> libraries = libraryController.getLibraryList();
            Platform.runLater(() -> showLibraryInUI(libraries));
        }).start();
    }


    /**
     * Apre un dialog per aggiungere il libro selezionato a una libreria esistente.
     * Se non esistono librerie, mostra un alert.
     *
     * @param event evento di azione (es. click su bottone)
     */
    @FXML
    private void addBookToLibrary(ActionEvent event) {
        try {
            // Ottieni la lista delle librerie dell'utente
            List<String> libraries = getLibraryNames();

            if (libraries.isEmpty()) {
                alertController.showAlert("Nessuna libreria", "Non hai ancora creato librerie. Creane una prima di aggiungere libri.");
                return;
            }

            // Carica il file FXML del dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookrecommender2/addToLibraryDialog.fxml"));
            Parent root = loader.load();

            // Ottieni il controller del dialog e imposta la lista delle librerie
            AddToLibraryDialogController controller = loader.getController();
            controller.setLibraries(libraries);

            // Crea il dialog e setta il contenuto
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane((DialogPane) root);
            dialog.setTitle("Aggiungi a Libreria");

            // Mostra il dialog e attendi la risposta
            Optional<ButtonType> result = dialog.showAndWait();

            // Se l'utente preme OK, aggiungi il libro alla libreria selezionata
            if (result.isPresent() && result.get() == ButtonType.OK) {
                String selectedLibrary = controller.getSelectedLibrary();

                if (selectedLibrary != null && !selectedLibrary.isEmpty()) {
                    libraryController.addBookToSelectedLibrary(selectedLibrary, selectedBookData);
                }
            }
        } catch (IOException e) {
            alertController.showAlert("Errore", "Impossibile aprire la finestra di dialogo: " + e.getMessage());
        }
    }

    /**
     * Apre un dialog per aggiungere una valutazione e recensione al libro selezionato.
     *
     * @param event evento di azione
     * @throws IOException in caso di errore di caricamento del dialog
     */
    @FXML
    private void addRating(ActionEvent event) throws IOException {
        // Carica il dialog per l'aggiunta delle recensioni
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookrecommender2/addRatingDialog.fxml"));
        Parent root = loader.load();

        // Ottieni il controller del dialog
        AddRatingDialogController controller = loader.getController();

        // Passa il libro selezionato al controller
        controller.setBook(selectedBookData);

        // Crea il dialog e setta il contenuto
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane((DialogPane) root);
        dialog.setTitle("Aggiungi Recensione");

        // Mostra il dialog e attendi il risultato
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Ottieni le valutazioni e la recensione inserite
            int styleRating = controller.getStyleRating();
            int contentRating = controller.getContentRating();
            int appealRating = controller.getAppealRating();
            int originalityRating = controller.getOriginalityRating();
            int editionRating = controller.getEditionRating();
            String reviewText = controller.getReviewText();
            int averageRating = controller.getAverageRating();

            // Controlla se la recensione è vuota
            if (styleRating < 1 || styleRating > 5 || contentRating < 1 || contentRating > 5 ||
                    appealRating < 1 || appealRating > 5 || originalityRating < 1 || originalityRating > 5 ||
                    editionRating < 1 || editionRating > 5) {
                alertController.showAlert("Errore", "Tutte le valutazioni devono essere tra 1 e 5");
            } else {
                if (reviewText.isEmpty()) {
                    alertController.showAlert("Errore", "La nota è vuota");
                } else {
                    ratingController.addRating(selectedBookData.getId(), styleRating, contentRating, appealRating,
                            originalityRating, editionRating, reviewText, averageRating);
                }
            }
        }
    }

    /**
     * Apre un dialog per mostrare la valutazione media del libro selezionato.
     *
     * @param event evento di input mouse
     * @throws IOException in caso di errore di caricamento del dialog
     */
    @FXML
    private void openRating(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookrecommender2/addRatingDialog.fxml"));
        Parent root = loader.load();

        AddRatingDialogController controller = loader.getController();
        controller.setBook(selectedBookData);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane((DialogPane) root);
        dialog.setTitle("Valutazione media");

        // Ottieni la prima valutazione aggregata dalla lista
        Rating ratingAggragato = ratingList.getFirst();

        // Imposta le valutazioni esistenti nel dialog
        controller.setExistingRatings(ratingAggragato.getStile(),
                ratingAggragato.getContenuto(),
                ratingAggragato.getGradevolezza(),
                ratingAggragato.getOriginalita(),
                ratingAggragato.getEdizione());

        // Nascondi il campo testo per la recensione
        controller.setReviewTextVisible(false);

        // Mostra il dialog e attendi risposta (anche se non usata qui)
        Optional<ButtonType> result = dialog.showAndWait();
    }

    /**
     * Apre un dialog per mostrare una valutazione specifica (non modificabile) con recensione.
     *
     * @param rating la valutazione da mostrare
     * @throws IOException in caso di errore di caricamento del dialog
     */
    @FXML
    private void openRating(Rating rating) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookrecommender2/addRatingDialog.fxml"));
        Parent root = loader.load();

        AddRatingDialogController controller = loader.getController();
        controller.setReviewTextEditable(false);

        controller.setBook(selectedBookData);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane((DialogPane) root);
        dialog.setTitle("Recensione di: " + rating.getUserId());

        controller.setExistingRatings(rating.getStile(),
                rating.getContenuto(),
                rating.getGradevolezza(),
                rating.getOriginalita(),
                rating.getEdizione());

        controller.setReviewTextVisible(true);
        controller.setReviewText(rating.getRecensione());

        Optional<ButtonType> result = dialog.showAndWait();
    }

    /**
     * Estrae e ritorna la lista dei nomi delle librerie dall'elenco delle librerie dell'utente.
     *
     * @return lista di nomi di librerie
     */
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

    /**
     * Carica i libri per la pagina home o suggeriti, usando la cache se disponibile,
     * e aggiorna la visualizzazione con la paginazione.
     *
     * @param currentPage identificatore della pagina corrente (es. "home", "suggested-books")
     */
    @FXML
    private void loadHomePageBooks(String currentPage) {
        // Se i libri sono già in cache e siamo nella pagina home
        if (bookCached.hasCachedHomeBooks() && currentPage.contains("home")) {
            currentSearchResults.addAll(bookCached.getCachedHomeBooks());

            updatePageDisplay();

            // Visualizza la pagina corrente nella UI
            Platform.runLater(this::displayCurrentPage);

        } else {
            BookClient client = new BookClient();
            try {
                if (currentPage.contains("home")) {
                    // Ottieni libri dalla prima pagina
                    List<Book> books = client.getBooks(0);
                    bookCached.setCachedHomeBooks(books);
                    currentSearchResults.addAll(books);

                    updatePageDisplay();

                    Platform.runLater(this::displayCurrentPage);

                } else if (currentPage.contains("suggested")) {
                    List<Book> books = new ArrayList<>();
                    // Ottieni libri per ogni libreria dell'utente
                    for (String library : getLibraryNames()) {
                        books.addAll(client.getLibraryBooks(UserManager.getUserId(), library));
                    }
                    currentSearchResults.addAll(books);

                    updatePageDisplay();

                    Platform.runLater(this::displayCurrentPage);
                }

            } catch (IOException e) {
                alertController.showAlert("Errore di connessione", "Impossibile connettersi al server: " + e.getMessage());
            }
        }
    }


    /**
     * Configura il campo di ricerca impostando l'azione da eseguire quando l'utente preme Invio.
     * Se il campo di ricerca è nullo, non fa nulla.
     */
    private void setupSearchField() {
        if (searchField == null) return;

        searchField.setOnAction(this::handleSearch);
    }

    /**
     * Gestisce l'evento di ricerca attivato dall'utente.
     * Esegue una ricerca in base al termine inserito e al tipo di ricerca selezionato.
     * Valida i dati di input e mostra eventuali messaggi di errore.
     * Esegue la ricerca in un thread separato e aggiorna l'interfaccia utente con i risultati.
     *
     * @param event Evento di azione generato dal campo di ricerca.
     */
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
        String year = null;

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
                year = yearField.getText().trim();
                if (yearField.getText().trim().isEmpty()) {
                    alertController.showAlert("Anno mancante", "Inserisci un anno per questo tipo di ricerca");
                    return;
                }
                try {
                    Integer.parseInt(yearField.getText().trim());
                } catch (NumberFormatException e) {
                    alertController.showAlert("Anno non valido", "Inserisci un anno valido");
                    return;
                }
                break;
            default:
                serverSearchType = "TITLE"; // Default se non riconosciuto
                break;
        }



        // Esegui la ricerca in un thread separato
        String finalServerSearchType = serverSearchType;
        String finalYear = year;
        new Thread(() -> {
            try {
                currentSearchResults = bookClient.performSearch(finalServerSearchType, searchTerm, finalYear);
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

    /**
     * Mostra il menu contestuale utente con l'opzione di logout.
     * Al logout cambia la scena alla vista non loggata.
     *
     * @param event Evento di mouse che attiva il menu.
     */
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

    /**
     * Mostra nella UI una lista di libri suggeriti.
     * Se la lista è vuota, mostra un messaggio di assenza suggerimenti.
     *
     * @param books Lista di libri suggeriti da visualizzare.
     */
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

    /**
     * Aggiorna la visualizzazione delle stelle di valutazione nella UI
     * colorando le stelle in base al voto medio disponibile.
     *
     * @param ratingStarsContainer Contenitore HBox che contiene le stelle da aggiornare.
     */
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

    /**
     * Avvia il caricamento e la visualizzazione delle recensioni associate al libro selezionato.
     */
    public void showRating() {
        loadAndDisplayBookReviews(selectedBookData.getId());
    }

    /**
     * Inizializza i dati relativi al libro selezionato aggiornando la UI con i dettagli,
     * valutazioni, suggerimenti e copertina. Gestisce anche la visibilità dei controlli
     * in base allo stato di login dell'utente.
     *
     * @param book Libro selezionato da visualizzare.
     */
    public void initBookData(Book book) {
        if (book == null) return;
        // Memorizza il libro selezionato
        selectedBookData = book;
        ratingList = ratingController.fetchRating(selectedBookData.getId());
        List<Book> suggestedBookList = suggestionController.getSuggestedBooks(selectedBookData.getId());
        //stampa i suggerimetni
        System.out.println("Libri suggeriti: " + suggestedBookList);
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

    /**
     * Carica e mostra le recensioni aggregate di un libro nel contenitore dedicato.
     * Se non ci sono recensioni mostra un messaggio di assenza.
     *
     * @param bookId ID del libro per cui caricare le recensioni.
     */
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


    /**
     * Crea una VBox che rappresenta una recensione.
     * La recensione contiene un'intestazione con l'ID utente e la valutazione sotto forma di stelle,
     * seguita da una sezione per i dettagli della valutazione.
     *
     * @param rating l'oggetto Rating contenente i dati della recensione, come l'ID utente e il voto finale
     * @return una VBox contenente la visualizzazione grafica della recensione
     */
    private VBox createReviewBox(Rating rating) {
        VBox reviewBox = new VBox(10);
        reviewBox.getStyleClass().add("review-box");

        // Intestazione con ID utente e valutazione a stelle
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label userLabel = new Label(rating.getUserId() + ":");
        userLabel.getStyleClass().add("review-user");

        HBox starsBox = createRatingStars(rating.getVotoFinale());

        header.getChildren().addAll(userLabel, starsBox);

        // Sezione per i dettagli della valutazione (attualmente vuota)
        GridPane ratingDetails = new GridPane();
        ratingDetails.setHgap(10);
        ratingDetails.setVgap(5);

        reviewBox.getChildren().addAll(header, ratingDetails);

        return reviewBox;
    }

    /**
     * Crea una HBox contenente stelle colorate per rappresentare la valutazione numerica.
     * Le stelle piene (fino al valore della valutazione) sono colorate in oro,
     * mentre le stelle vuote sono colorate in grigio.
     *
     * @param rating valore numerico della valutazione da 0 a 5
     * @return una HBox contenente 5 icone a forma di stella con il colore appropriato
     */
    private HBox createRatingStars(int rating) {
        HBox starsBox = new HBox(5);
        starsBox.setAlignment(Pos.CENTER_LEFT);

        for (int i = 1; i <= 5; i++) {
            FontIcon star = new FontIcon();
            star.setIconLiteral("fas-star");
            star.setIconSize(16);

            if (i <= rating) {
                star.setIconColor(Color.GOLD);  // stelle piene
            } else {
                star.setIconColor(Color.GRAY);  // stelle vuote
            }

            starsBox.getChildren().add(star);
        }

        return starsBox;
    }

    /**
     * Aggiunge un elemento visivo rappresentante un libro all'interfaccia utente.
     * La visualizzazione e il comportamento dell'elemento dipendono dalla pagina corrente.
     *
     * <p>Gestisce tre contesti principali:</p>
     * <ul>
     *   <li>Pagina "suggested-books": aggiunge libri con checkbox per selezione, escludendo il libro già selezionato.</li>
     *   <li>Pagina "home" o "library-books": aggiunge libri cliccabili che permettono di passare alla vista dettagliata del libro.</li>
     *   <li>Pagina "book-view.fxml": aggiunge libri consigliati cliccabili per la navigazione al dettaglio.</li>
     * </ul>
     *
     * @param book il libro da visualizzare nell'interfaccia utente
     */
    private void addBookToUI(Book book) {
        if (SceneController.currentPage.contains("suggested-books")) {
            // Se il libro è lo stesso di quello selezionato, non fare nulla
            if (book.getId() == selectedBookData.getId()) {
                // Nessuna azione
            } else {
                // Crea l'elemento visuale per il libro con checkbox
                HBox bookItem = new HBox();
                bookItem.getStyleClass().add("book-item");
                bookItem.setSpacing(15.0);

                // Imposta l'immagine di copertina
                ImageView coverView = new ImageView();
                coverView.setFitWidth(120.0);
                coverView.setFitHeight(180.0);
                coverView.setPreserveRatio(true);

                // Carica l'immagine o fallback se non disponibile
                if (!book.getCoverUrl().equals("null")) {
                    try {
                        String imageUrl = book.getCoverUrl();
                        Image coverImage = new Image(imageUrl, true);
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

                // Contenitore per dettagli testuali e checkbox
                VBox contentBox = new VBox();
                contentBox.setUserData(book.getId()); // memorizza l'ID del libro
                contentBox.setAlignment(Pos.CENTER_LEFT);
                contentBox.setSpacing(10.0);

                // Etichette con informazioni del libro
                Label titleLabel = new Label(book.getTitle());
                titleLabel.getStyleClass().add("book-title");

                Label authorLabel = new Label("Autore: " + (book.getAuthor().length() >= 3 ? book.getAuthor().substring(3) : book.getAuthor()));
                authorLabel.getStyleClass().add("book-author");

                Label categoryLabel = new Label("Categoria: " + book.getCategory());
                categoryLabel.getStyleClass().add("book-category");

                Label publisherLabel = new Label("Editore: " + book.getPublisher());
                publisherLabel.getStyleClass().add("book-publisher");

                Label yearLabel = new Label("Anno: " + book.getPublicationYear());
                yearLabel.getStyleClass().add("book-year");

                // Checkbox per selezionare il libro
                CheckBox checkBox = new CheckBox();
                checkBox.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
                checkBox.setSelected(false);
                checkBox.setOnAction(e -> updateAddSuggestionsButtonState());

                // Aggiungi checkbox e dettagli al contenitore
                contentBox.getChildren().add(checkBox);
                contentBox.getChildren().addAll(titleLabel, authorLabel, categoryLabel, publisherLabel, yearLabel);

                // Aggiungi copertina e contenuto all'elemento libro
                bookItem.getChildren().addAll(coverView, contentBox);
                bookItem.setCursor(Cursor.DEFAULT); // non cliccabile

                // Aggiungi l'elemento al container della lista libri suggeriti
                booksContainer.getChildren().add(bookItem);
            }

        } else if (SceneController.currentPage.contains("home") || SceneController.currentPage.contains("library-books")) {
            // Crea un elemento libro cliccabile senza checkbox
            HBox bookItem = new HBox();
            bookItem.getStyleClass().add("book-item");
            bookItem.setSpacing(15.0);

            ImageView coverView = new ImageView();
            coverView.setFitWidth(120.0);
            coverView.setFitHeight(180.0);
            coverView.setPreserveRatio(true);

            if (!book.getCoverUrl().equals("null")) {
                try {
                    Image coverImage = new Image(book.getCoverUrl(), true);
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

            VBox contentBox = new VBox();
            contentBox.setAlignment(Pos.CENTER_LEFT);
            contentBox.setSpacing(10.0);

            Label titleLabel = new Label(book.getTitle());
            titleLabel.getStyleClass().add("book-title");

            Label authorLabel = new Label("Autore: " + (book.getAuthor().length() >= 3 ? book.getAuthor().substring(3) : book.getAuthor()));
            authorLabel.getStyleClass().add("book-author");

            Label categoryLabel = new Label("Categoria: " + book.getCategory());
            categoryLabel.getStyleClass().add("book-category");

            Label publisherLabel = new Label("Editore: " + book.getPublisher());
            publisherLabel.getStyleClass().add("book-publisher");

            Label yearLabel = new Label("Anno: " + book.getPublicationYear());
            yearLabel.getStyleClass().add("book-year");

            contentBox.getChildren().addAll(titleLabel, authorLabel, categoryLabel, publisherLabel, yearLabel);

            bookItem.getChildren().addAll(coverView, contentBox);

            bookItem.setCursor(Cursor.HAND);
            bookItem.setOnMouseClicked(event -> {
                selectedBookData = book;
                switchToBookView(event);
            });

            booksContainer.getChildren().add(bookItem);

        } else if (SceneController.currentPage.contains("book-view.fxml")) {
            // Aggiunge libri consigliati nella vista dettaglio libro
            HBox bookItem = new HBox();
            bookItem.getStyleClass().add("book-item");
            bookItem.setSpacing(15.0);

            ImageView coverView = new ImageView();
            coverView.setFitWidth(120.0);
            coverView.setFitHeight(180.0);
            coverView.setPreserveRatio(true);

            if (!book.getCoverUrl().equals("null")) {
                try {
                    String imageUrl = book.getCoverUrl();
                    Image coverImage = new Image(imageUrl, true);
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

            VBox contentBox = new VBox();
            contentBox.setAlignment(Pos.CENTER_LEFT);
            contentBox.setSpacing(10.0);

            Label titleLabel = new Label(book.getTitle());
            titleLabel.getStyleClass().add("book-title");

            Label authorLabel = new Label("Autore: " + (book.getAuthor().length() >= 3 ? book.getAuthor().substring(3) : book.getAuthor()));
            authorLabel.getStyleClass().add("book-author");

            Label categoryLabel = new Label("Categoria: " + book.getCategory());
            categoryLabel.getStyleClass().add("book-category");

            Label publisherLabel = new Label("Editore: " + book.getPublisher());
            publisherLabel.getStyleClass().add("book-publisher");

            Label yearLabel = new Label("Anno: " + book.getPublicationYear());
            yearLabel.getStyleClass().add("book-year");

            contentBox.getChildren().addAll(titleLabel, authorLabel, categoryLabel, publisherLabel, yearLabel);

            bookItem.getChildren().addAll(coverView, contentBox);
            bookItem.setCursor(Cursor.HAND);
            bookItem.setOnMouseClicked(event -> {
                selectedBookData = book;
                switchToBookView(event);
            });

            libriConsigliatiContainer.getChildren().add(bookItem);
        }
    }


    /**
     * Gestisce la navigazione alla pagina successiva nella paginazione.
     * Incrementa il numero della pagina corrente se non si è già all'ultima pagina,
     * aggiorna i controlli di paginazione e visualizza i libri della nuova pagina.
     *
     * @param event L'evento di azione generato dal pulsante "pagina successiva".
     */
    @FXML
    protected void goToNextPage(ActionEvent event) {
        if (currentPage < getTotalPages()) {
            currentPage++;
            updatePaginationControls();
            displayCurrentPage();
        }
    }

    /**
     * Gestisce la navigazione alla pagina precedente nella paginazione.
     * Decrementa il numero della pagina corrente se non si è già alla prima pagina,
     * aggiorna i controlli di paginazione e visualizza i libri della nuova pagina.
     *
     * @param event L'evento di azione generato dal pulsante "pagina precedente".
     */
    @FXML
    protected void goToPrevPage(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            updatePaginationControls();
            displayCurrentPage();
        }
    }

    /**
     * Calcola il numero totale di pagine in base al numero totale di risultati della ricerca
     * e al numero di libri visualizzati per pagina.
     *
     * @return Il numero totale di pagine.
     */
    private int getTotalPages() {
        return (int) Math.ceil((double) currentSearchResults.size() / booksPerPage);
    }

    /**
     * Aggiorna lo stato e il testo dei controlli di paginazione,
     * inclusi i pulsanti "pagina precedente", "pagina successiva" e l'etichetta della pagina.
     */
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

    /**
     * Aggiorna la visualizzazione della pagina attuale,
     * incluso l'aggiornamento dell'etichetta pagina e lo stato dei pulsanti di navigazione.
     */
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

    /**
     * Visualizza i libri corrispondenti alla pagina corrente all'interno del contenitore UI.
     * Pulisce il contenitore e aggiunge gli elementi relativi ai libri della pagina corrente.
     */
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

    /**
     * Mostra la lista delle librerie nella UI come pulsanti cliccabili.
     * Ogni pulsante contiene un'icona, il nome della libreria e uno stile personalizzato.
     * Se la lista è vuota, mostra un messaggio informativo.
     *
     * @param libraryNames Lista di stringhe che rappresentano le librerie, con formato specifico.
     */
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


    /**
     * Apre una finestra di dialogo per creare una nuova libreria.
     * Verifica che il nome inserito sia valido e, in caso positivo, crea la libreria.
     * Altrimenti mostra un messaggio di errore.
     *
     * @param event Evento generato dal click sul pulsante "Crea Nuova Libreria".
     */
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

                // Verifica che il nome non sia vuoto e non contenga ":"
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

    /**
     * Inizializza la visualizzazione dei libri di una libreria specifica.
     * Salva il nome della libreria corrente e carica i suoi libri.
     *
     * @param libraryName Nome della libreria da caricare.
     */
    public void initLibraryBooksView(String libraryName) {
        // Salva il nome della libreria corrente
        this.currentLibraryName = libraryName;

        // Carica i libri della libreria
        loadLibraryBooks(currentLibraryName);
    }

    /**
     * Carica i libri di una libreria specifica in un thread separato per non bloccare la UI.
     * Aggiorna la UI con i libri ottenuti o mostra un messaggio se la libreria è vuota.
     *
     * @param libraryName Nome della libreria di cui caricare i libri.
     */
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

    /**
     * Gestisce l'aggiunta di suggerimenti in base ai libri selezionati dall'utente.
     * Raccoglie gli ID dei libri selezionati tramite checkbox, chiama il controller di suggerimenti
     * e mostra messaggi di conferma o errore.
     *
     * @param event Evento generato dal click sul pulsante "Aggiungi Suggerimenti".
     */
    @FXML
    private void addSuggestions(ActionEvent event) {
        List<Integer> selectedBooks = new ArrayList<>();

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
                                    Integer bookId = (Integer) contentBox.getUserData(); // <-- prendi l'ID qui
                                    selectedBooks.add(bookId);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (suggestionController.addSuggestedBook(UserManager.getUserId(), selectedBookData.getId(), selectedBooks)) {
            alertController.showAlertSucces("Suggerimenti aggiunti", "I libri selezionati sono stati suggeriti con successo.");
            // Torna nella schermata principale
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
     * Aggiorna lo stato del pulsante "Aggiungi Suggerimenti" in base al numero di libri selezionati.
     * Abilita il pulsante solo se sono selezionati tra 1 e 3 libri.
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

    /**
     * Deseleziona tutte le checkbox dei libri nella UI,
     * aggiorna lo stato del pulsante "Aggiungi Suggerimenti" e torna alla schermata principale.
     *
     * @param event Evento generato dal click sul pulsante "Cancella Selezione".
     */
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