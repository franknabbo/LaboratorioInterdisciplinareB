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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private TextField searchField;
    @FXML
    private VBox booksContainer; // Cambiato da FlowPane a VBox
    @FXML
    private FontIcon star1, star2, star3, star4, star5;
    @FXML
    private Label usernameLabel;

    @FXML
    private ComboBox<String> searchTypeCombo;
    @FXML
    private TextField yearField;
    @FXML
    private Button searchButton;
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

            // Mostra/nascondi il campo anno in base alla selezione
            searchTypeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && yearField != null) {
                    yearField.setVisible(newVal.equals("Per autore e anno"));
                }
            });
        }

        // Configurazione del campo di ricerca
        setupSearchField();

        // Carica i libri solo se il container è disponibile
        if (booksContainer != null) {
            loadBooks();
        }
    }

    @FXML
    private void loadBooks() {
        // Pulisci il container dei libri
        if (booksContainer == null) return;
        booksContainer.getChildren().clear();

        // Utilizza BookClient per ottenere i libri
        try {
            BookClient client = new BookClient();
            try {
                // Richiedi 10 libri al server
                List<Book> books = client.getBookCovers(10);

                // Visualizza i libri ricevuti nell'interfaccia
                for (Book book : books) {
                    // Aggiungi il libro all'interfaccia
                    Platform.runLater(() -> addBookToUI(book));
                }
            } finally {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupSearchField() {
        if (searchField == null) return;

        searchField.setOnAction(event -> handleSearch());
    }


    @FXML
    private void handleSearch() {
        if (searchField == null) return;

        String searchTerm = searchField.getText();
        if (searchTerm.isEmpty()) {
            return;
        }

        // Determina il tipo di ricerca
        String searchType = "TITLE"; // Default
        if (searchTypeCombo != null) {
            String selectedType = searchTypeCombo.getValue();
            if (selectedType != null) {
                if (selectedType.equals("Per autore")) {
                    searchType = "AUTHOR";
                } else if (selectedType.equals("Per autore e anno")) {
                    searchType = "AUTHOR_YEAR";
                    // Controlla che l'anno sia stato inserito
                    if (yearField != null && yearField.getText().isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Campo mancante");
                        alert.setHeaderText("Anno richiesto");
                        alert.setContentText("Per questo tipo di ricerca è necessario inserire l'anno.");
                        alert.showAndWait();
                        return;
                    }
                }
            }
        }

        // Pulisci il container dei libri
        if (booksContainer != null) {
            booksContainer.getChildren().clear();
        }

        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Costruisci e invia la richiesta di ricerca appropriata
            String searchRequest;
            if (searchType.equals("AUTHOR_YEAR") && yearField != null) {
                searchRequest = "SEARCH:" + searchType + ":" + searchTerm + ":" + yearField.getText();
            } else {
                searchRequest = "SEARCH:" + searchType + ":" + searchTerm;
            }
            out.println(searchRequest);

            // Elabora la risposta del server
            String line;
            boolean reading = false;
            List<Book> books = new ArrayList<>();

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
                        String[] parts = line.split(":", 5);
                        if (parts.length >= 5) {
                            Book book = new Book(parts[1], parts[2], parts[3]);
                            book.setCoverUrl(parts[4]);
                            books.add(book);
                        }
                    } catch (Exception e) {
                        System.err.println("Errore nel parsing: " + e.getMessage());
                    }
                }
            }

            // Visualizza i risultati
            for (Book book : books) {
                Platform.runLater(() -> addBookToUI(book));
            }

            if (books.isEmpty()) {
                Platform.runLater(() -> {
                    Label noResultsLabel = new Label("Nessun libro trovato per: " + searchTerm);
                    noResultsLabel.getStyleClass().add("no-results-label");
                    booksContainer.getChildren().add(noResultsLabel);
                });
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di connessione");
            alert.setHeaderText("Impossibile connettersi al server per la ricerca");
            alert.setContentText("Dettagli: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
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
        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty() && !book.getCoverUrl().equals("null")) {
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


        // Aggiungi gli elementi testuali al contentBox
        contentBox.getChildren().addAll(titleLabel, authorLabel);

        // Aggiungi copertina e contenitore di testo all'elemento libro
        bookItem.getChildren().addAll(coverView, contentBox);

        // Aggiungi al container
        booksContainer.getChildren().add(bookItem);
    }
}