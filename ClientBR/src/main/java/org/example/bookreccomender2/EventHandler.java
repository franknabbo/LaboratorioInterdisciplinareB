package org.example.bookreccomender2;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.scene.image.ImageView;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EventHandler {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField taxCodeField;
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
    private Button searchButton;
    @FXML
    private Button prevPageButton;
    @FXML
    private Button nextPageButton;
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
    private ImageView coverImage;
    private int currentPage = 1;
    private int booksPerPage = 25;
    private List<Book> currentSearchResults = new ArrayList<>();
    private List<Book> allBooks = new ArrayList<>(); // Tutti i libri dalla ricerca
    private Book selectedBook;

    @FXML
    private TitledPane recensioneMiaPane;
    @FXML
    private TitledPane consigliMieiPane;
    @FXML
    private Button addToLibraryButton;

    @FXML
    protected void switchToRegister(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/bookreccomender2/register-view.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void switchToHome(ActionEvent event) {
        try {
            String viewFile = SessionManager.isLoggedIn() ?
                    "/org/example/bookreccomender2/homeLogged-view.fxml" :
                    "/org/example/bookreccomender2/homeNotLogged-view.fxml";

            Parent root = FXMLLoader.load(getClass().getResource(viewFile));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void switchToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/bookreccomender2/login-view.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchToBookView(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bookreccomender2/Book-view.fxml"));
            Parent root = loader.load();

            // Ottieni il controller e inizializza i dati del libro
            EventHandler controller = loader.getController();
            controller.initBookData(selectedBook);

            // Cambia scena
            Scene scene = ((Node) event.getSource()).getScene();
            Stage stage = (Stage) scene.getWindow();
            scene = new Scene(root, 700, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile aprire la pagina del libro: " + e.getMessage());
        }
    }

    @FXML
    protected void loginUser(ActionEvent event) {
        String userId = userIdField.getText();
        String password = passwordField.getText();

        if (userId.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Campi mancanti");
            alert.setContentText("Inserisci userId e password.");
            alert.showAndWait();
            return;
        }

        // Crittografia della password
        String encryptedPassword = encryptPassword(password);

        // Connessione al server
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Leggi il messaggio di benvenuto
            String benvenuto = in.readLine();

            // Invio della richiesta di login
            out.println("LOGIN:" + userId + ":" + encryptedPassword);

            // Gestione della risposta
            String risposta = in.readLine();

            if (risposta.startsWith("LOGIN OK")) {
                // Estrai l'userId dalla risposta (formato: "LOGIN OK:userId")
                String loggedUserId = risposta.split(":")[1];

                // Imposta lo stato di login
                SessionManager.login(loggedUserId);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Completato");
                alert.setHeaderText("Login avvenuto con successo!");
                alert.setContentText("Benvenuto, " + loggedUserId);
                alert.showAndWait();

                // Reindirizza alla home page dopo il login
                switchToHome(event);
            } else {
                String errorMessage = "Credenziali non valide.";
                if (risposta.contains(":")) {
                    errorMessage = risposta.split(":", 2)[1];
                }

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Login fallito");
                alert.setContentText(errorMessage);
                alert.showAndWait();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di connessione");
            alert.setHeaderText("Impossibile connettersi al server");
            alert.setContentText("Dettagli: " + e.getMessage());
            alert.showAndWait();

            System.out.println("Errore di connessione al server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void registerUser(ActionEvent event) {
        // Raccogli i dati dai campi di input
        String nome = firstNameField.getText();
        String cognome = lastNameField.getText();
        String codiceFiscale = taxCodeField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Controlla che i campi non siano vuoti
        if (nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Campi mancanti");
            alert.setContentText("Tutti i campi sono obbligatori.");
            alert.showAndWait();
            return;
        }

        // Connessione al server
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Leggi il messaggio di benvenuto
            String benvenuto = in.readLine();
            System.out.println(benvenuto);

            // Crittografia della password
            String encryptedPassword = encryptPassword(password);

            // Invio della richiesta di registrazione con password crittografata
            out.println("REGISTER:" + nome + ":" + cognome + ":" + codiceFiscale + ":" + email + ":" + encryptedPassword);

            // Gestione della risposta
            String risposta = in.readLine();
            System.out.println("Risposta dal server: " + risposta);

            if (risposta.startsWith("REGISTRAZIONE OK")) {
                // Estrai l'userId dalla risposta
                String userId = risposta.split(":")[1];

                SessionManager.login(userId);

                // Mostra alert di successo con userId
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registrazione completata");
                alert.setHeaderText("Registrazione avvenuta con successo!");
                alert.setContentText("Il tuo userId è: " + userId + "\n\nUtilizza questo userId per accedere al sistema.");
                alert.showAndWait();

                // Reindirizza alla pagina di login
                switchToHome(event);
            } else {
                // Mostra alert di errore
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Registrazione fallita");
                alert.setContentText("Non è stato possibile completare la registrazione.\n" +
                        (risposta.contains(":") ? risposta.split(":", 2)[1] : ""));
                alert.showAndWait();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di connessione");
            alert.setHeaderText("Impossibile connettersi al server");
            alert.setContentText("Dettagli: " + e.getMessage());
            alert.showAndWait();

            System.out.println("Errore di connessione al server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Metodo per crittografare la password con SHA-256
    private String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Errore durante la crittografia: " + e.getMessage());
            e.printStackTrace();
            return password; // Fallback in caso di errore
        }
    }

    @FXML
    protected void logout(ActionEvent event) {
        SessionManager.logout();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/bookreccomender2/homeNotLogged-view.fxml"));

            // Ottieni lo Stage usando una strategia alternativa
            Stage stage = null;

            // Prova a ottenere lo stage dall'evento
            if (event.getSource() instanceof Node) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            }
            // Se non funziona, prova a usare un elemento sempre presente nella scena
            else if (usernameLabel != null && usernameLabel.getScene() != null) {
                stage = (Stage) usernameLabel.getScene().getWindow();
            }

            if (stage != null) {
                Scene scene = new Scene(root, 700, 700);
                stage.setScene(scene);
                stage.show();
            } else {
                // Fallback se non riusciamo a trovare lo stage
                System.err.println("Impossibile trovare lo stage per la navigazione");
            }
        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Errore di navigazione");
            alert.setContentText("Impossibile tornare alla home.\nDettagli: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void initialize() {
        if (usernameLabel != null && SessionManager.isLoggedIn()) {
            usernameLabel.setText(SessionManager.getUserId());
        }

        // Configura il ComboBox per il tipo di ricerca
        if (searchTypeCombo != null) {
            searchTypeCombo.getSelectionModel().selectFirst(); // Seleziona "Per titolo" come default

            searchTypeCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && newValue.equals("Per autore e anno")) {
                    yearField.setVisible(true);
                } else {
                    yearField.setVisible(false);
                }
            });
        }

        // Configurazione del campo di ricerca
        setupSearchField();

        // Carica i libri solo se il container è disponibile
        if (booksContainer != null) {
            loadHomePageBooks();
        }
    }

    @FXML
    private void loadHomePageBooks() {
        // Utilizza BookClient per ottenere i libri
        try {
            BookClient client = new BookClient();
            try {
                // Richiedi libri al server
                List<Book> books = client.getBooks(0);
                //stampa books


                currentSearchResults.addAll(books);

                // Aggiorna controlli di paginazione
                updatePageDisplay();

                // Visualizza prima pagina
                Platform.runLater(this::displayCurrentPage);
            } finally {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupSearchField() {
        if (searchField == null) return;

        searchField.setOnAction(event -> handleSearch(event));
    }

    @FXML
    protected void handleSearch(ActionEvent event) {
        // Resetta la paginazione
        currentPage = 1;

        String searchTerm = searchField.getText().trim();
        String searchType = searchTypeCombo.getValue();

        // Verifica se i campi sono compilati correttamente
        if (searchTerm.isEmpty()) {
            // Mostra un messaggio di errore o carica tutti i libri
            showAlert("Ricerca vuota", "Inserisci un termine di ricerca");
            return;
        }

        if (searchType == null) {
            showAlert("Tipo di ricerca mancante", "Seleziona un tipo di ricerca");
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
                    showAlert("Anno mancante", "Inserisci un anno per questo tipo di ricerca");
                    return;
                }
                break;
            default:
                showAlert("Tipo di ricerca non valido", "Seleziona un tipo di ricerca valido");
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
                currentSearchResults = performSearch(serverSearchType, searchTerm, year);

                // Aggiorna l'UI nel thread JavaFX
                Platform.runLater(() -> {
                    displayCurrentPage();
                    updatePaginationControls();
                    // showLoadingIndicator(false);
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showAlert("Errore di connessione", "Impossibile connettersi al server: " + e.getMessage());
                    // showLoadingIndicator(false);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void displayCurrentPageBooks() {
        booksContainer.getChildren().clear();

        int startIndex = (currentPage - 1) * booksPerPage;
        int endIndex = Math.min(startIndex + booksPerPage, allBooks.size());

        for (int i = startIndex; i < endIndex; i++) {
            Book book = allBooks.get(i);
            addBookToUI(book);
        }
    }


    @FXML
    private void showUserMenu(MouseEvent event) {
        // Crea menu contestuale
        ContextMenu contextMenu = new ContextMenu();

        // Crea l'opzione per il logout
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setGraphic(new FontIcon("fas-sign-out-alt"));

        // Associa l'azione al click sull'elemento
        logoutItem.setOnAction(e -> logout(new ActionEvent()));

        // Aggiungi l'opzione al menu
        contextMenu.getItems().add(logoutItem);

        // Mostra il menu nel punto del click
        contextMenu.show(((Node) event.getSource()).getScene().getWindow(),
                event.getScreenX(), event.getScreenY());
    }

    public void initBookData(Book book) {
        if (book == null) return;

        // Memorizza il libro selezionato
        this.selectedBook = book;

        // Aggiorna i campi della vista con i dati del libro
        if (titoloLabel != null) titoloLabel.setText(book.getTitle());
        if (autoreLabel != null) autoreLabel.setText(book.getAuthor());
        if (genereLabel != null) genereLabel.setText(book.getCategory());
        if (editoreLabel != null) editoreLabel.setText(book.getPublisher());
        if (annoLabel != null) annoLabel.setText(book.getPublicationYear());

        // Gestisci la visibilità delle sezioni in base allo stato di login
        if (recensioneMiaPane != null) {
            recensioneMiaPane.setVisible(SessionManager.isLoggedIn());
            recensioneMiaPane.setManaged(SessionManager.isLoggedIn());
        }

        if (consigliMieiPane != null) {
            consigliMieiPane.setVisible(SessionManager.isLoggedIn());
            consigliMieiPane.setManaged(SessionManager.isLoggedIn());
        }
        if (addToLibraryButton != null) {
            addToLibraryButton.setVisible(SessionManager.isLoggedIn());
            addToLibraryButton.setManaged(SessionManager.isLoggedIn());
        }


        // Carica la copertina
        if (coverImage != null) {
            if (book.getCoverUrl() != null && !book.getCoverUrl().equals("null")) {
                try {
                    // Estrai l'URL effettivo
                    String imageUrl = book.getCoverUrl();
                    int indexOfHttps = imageUrl.indexOf("https://");
                    int indexOfHttp = imageUrl.indexOf("http://");

                    if (indexOfHttps != -1) {
                        imageUrl = imageUrl.substring(indexOfHttps);
                    } else if (indexOfHttp != -1) {
                        imageUrl = imageUrl.substring(indexOfHttp);
                    } else if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                        imageUrl = "/" + imageUrl;
                    }

                    // Carica l'immagine
                    Image cover = new Image(imageUrl, true);
                    coverImage.setImage(cover);
                } catch (Exception e) {
                    coverImage.setImage(new Image("/logoBookRecomender.png"));
                }
            } else {
                coverImage.setImage(new Image("/logoBookRecomender.png"));
            }
        }
    }

    private void addBookToUI(Book book) {
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
                // Estrai l'URL effettivo dall'URL ricevuto
                String imageUrl = book.getCoverUrl();
                int indexOfHttps = imageUrl.indexOf("https://");
                int indexOfHttp = imageUrl.indexOf("http://");

                if (indexOfHttps != -1) {
                    imageUrl = imageUrl.substring(indexOfHttps);
                } else if (indexOfHttp != -1) {
                    imageUrl = imageUrl.substring(indexOfHttp);
                } else if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                    imageUrl = "/" + imageUrl; // Aggiungi slash per risorse locali
                }

                // Crea variabile finale per l'URL da usare nella lambda
                final String finalImageUrl = imageUrl;

                // Caricamento immagine
                Image coverImage = new Image(finalImageUrl, true);

                // Listener per errori
                coverImage.errorProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        Platform.runLater(() -> coverView.setImage(new Image("/logoBookRecomender.png")));
                    }
                });

                coverView.setImage(coverImage);
            } catch (Exception e) {
                coverView.setImage(new Image("/logoBookRecomender.png"));
            }
        } else {
            coverView.setImage(new Image("/logoBookRecomender.png"));
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

        // Aggiungi al container
        booksContainer.getChildren().add(bookItem);
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
        pageLabel.setText("Pagina " + currentPage + " di " + totalPages);
        nextPageButton.setDisable(currentPage >= totalPages);
        prevPageButton.setDisable(currentPage <= 1);
    }

    private void updatePageDisplay() {
        if (pageLabel != null) {
            pageLabel.setText("Pagina " + currentPage);
        }

        // Aggiorna stato dei pulsanti
        if (prevPageButton != null) {
            prevPageButton.setDisable(currentPage <= 1);
        }

        int totalPages = getTotalPages();
        if (nextPageButton != null) {
            nextPageButton.setDisable(currentPage >= totalPages);
        }
        updatePaginationControls();

    }

    private List<Book> performSearch(String searchType, String searchTerm, String year) throws IOException {
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Leggi il messaggio di benvenuto
            String welcome = in.readLine();

            // Invia la richiesta di ricerca
            String request = "SEARCH:" + searchType + ":" + searchTerm;
            if (year != null && searchType.equals("AUTHOR_YEAR")) {
                request += ":" + year;
            }
            out.println(request);

            // Parsa i risultati
            return parseSearchResults(in);
        }
    }

    private List<Book> parseSearchResults(BufferedReader in) throws IOException {
        List<Book> results = new ArrayList<>();
        String line;
        boolean reading = false;

        while ((line = in.readLine()) != null) {
            if (line.equals("INIZIO_LISTA_LIBRI")) {
                reading = true;
                continue;
            }

            if (line.equals("END_BOOKS")) {
                break;
            }

            if (reading && line.startsWith("BOOK:")) {
                try {
                    // Formato corretto da server: BOOK:titolo|||autore|||categoria|||editore|||anno_pubblicazione|||copertina
                    String[] parts = line.split("BOOK:|\\|\\|\\|");
                    if (parts.length >= 7) {
                        String title = parts[1];
                        String author = parts[2];
                        String category = parts[3];
                        String publisher = parts[4];
                        String publicationYear = parts[5];
                        String coverUrl = parts[6];
                        // Crea un oggetto Book e aggiungilo alla lista
                        Book book = new Book(title, author, category, publisher, publicationYear, coverUrl);
                        results.add(book);
                    }
                } catch (Exception e) {
                    System.err.println("Errore nel parsing: " + e.getMessage());
                }
            }
        }

        return results;
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
}