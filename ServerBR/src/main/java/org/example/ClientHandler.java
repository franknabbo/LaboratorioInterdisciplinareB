// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package org.example;

import org.example.db.*;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final BookDAO bookDAO;
    private final UtenteDAO utenteDAO;
    private LibraryDAO libraryDAO;
    private SuggestedBookDAO suggestedBookDAO;
    private final RatingDAO ratingDAO;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.bookDAO = new BookDAO();
        this.utenteDAO = new UtenteDAO();
        this.libraryDAO = new LibraryDAO();
        this.suggestedBookDAO = new SuggestedBookDAO();
        this.ratingDAO = new RatingDAO();
    }

    @Override
    public void run() {
        try {
            // Inizializzazione canali di comunicazione
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Ciclo di gestione richieste
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String response = processRequest(inputLine);
                out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Errore I/O con il client: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private String processRequest(String request) {
        if (request.startsWith("REGISTER:")) {
            return handleRegistration(request);
        } else if (request.startsWith("LOGIN:")) {
            return handleLogin(request);
        } else if (request.startsWith("GET_BOOKS")) {
            return handleGetBook(request);
        } else if (request.startsWith("SEARCH:")) {
            return handleSearch(request);
        } else if (request.startsWith("CREATE_LIBRARY:")) {
            return handleCreateLibrary(request);
        } else if (request.startsWith("GET_LIBRARY:")) {
            return handleGetLibrary(request);
        } else if (request.startsWith("ADD_BOOK_TO_LIBRARY")) {
            return handleAddBookToLibrary(request);
        } else if (request.startsWith("GET_LIBRARY_BOOKS:")) {
            return handleGetLibraryBooks(request);
        } else if (request.startsWith("ADD_RATING:")) {
            return handleAddRating(request);
        } else if (request.startsWith("GET_RATING:")) {
            return handleGetRatingFromBook(request);
        } else if(request.startsWith(("ADD_SUGGESTED_BOOK:"))) {
            return handleAddSuggestedBook(request);
        } else if(request.startsWith(("GET_SUGGESTED_BOOKS:"))) {
            return handleGetSuggestedBooks(request);
        } else {
            return "ERRORE:Comando non riconosciuto";
        }
    }

    private String handleAddSuggestedBook(String request) {

        // Formato: ADD_SUGGESTED_BOOK:userId:idLibroReferenced:idLibroSuggested:idLibroSuggested2:idLibroSuggested3:...
        //rimuovi add_suggested_book: dalla stringa request

        request = request.replace("ADD_SUGGESTED_BOOK:", "");

        String[] parts = request.split(":");

        String userId = parts[0];
        int idLibroReferenced = Integer.parseInt(parts[1]);
        int idLibroSuggested = Integer.parseInt(parts[2]);

        List<Integer> idLibri = new ArrayList<>();
        for(int i = 2; i < parts.length; i++){
            idLibri.add(Integer.parseInt(parts[i]));
        }
        //controllo se  il libro è nella libreria dell'utente
        boolean flag = false;
        for (Library library : libraryDAO.getUserLibraries(userId)) {
            if (libraryDAO.bookExistsInLibrary(library.getIdLibreria(), idLibroReferenced)) {
                flag = true;
                break;
            }
        }

        // Aggiungi il libro suggerito
        if(flag && suggestedBookDAO.addSuggestedBook(userId, idLibroReferenced, idLibri)) {
            return "SUGGESTION_SUCCESS:Libro suggerito aggiunto con successo";
        }else{
            return "ADD_SUGGESTED_BOOK_FAILED:Errore durante l'aggiunta del libro suggerito";
        }

    }

    private String handleGetSuggestedBooks(String request) {
        // Formato:  GET_SUGGESTED_BOOKS:idLibro
        String[] parts = request.split(":");
        if (parts.length < 2) {
            return "SUGGESTION_RETRIEVAL_FAILED:Parametri insufficienti";
        }
        int idLibro = Integer.parseInt(parts[1]);
        List<Integer> suggestedBooks = suggestedBookDAO.getSuggestedBooks(idLibro);
        if (suggestedBooks == null) {
            return "SUGGESTION_RETRIEVAL_FAILED:Nessun libro suggerito trovato per il libro con ID: " + idLibro;
        }
        List<Book> books = new ArrayList<>();
        for (Integer suggestedBook : suggestedBooks) {
            // Aggiungi il libro suggerito alla risposta
            books.add(bookDAO.getBookDetails(suggestedBook));
        }
        return getString(books);
    }

    private String handleAddRating(String request) {
        try {
            boolean flag = false;
            // Formato: ADD_RATING:idUtente|||idLibro|||stile|||contenuto|||gradevolezza|||originalit|||edizione|||votofinale|||recensione
            //rimuovi add_rating: dalla stringa request
            String[] parts = request.split(":");
            if (parts.length < 2) {
                return "RATING_FAILED:Parametri insufficienti";
            }
            String[] parts2 = parts[1].split("\\|\\|\\|");


            if (parts2.length < 9) {
                return "RATING_FAILED:Parametri insufficienti";
            }
            // Parsa i parametr

            String idUtente = parts2[0];
            int idLibro = Integer.parseInt(parts2[1]);
            int stile = Integer.parseInt(parts2[2]);
            int contenuto = Integer.parseInt(parts2[3]);
            int gradevolezza = Integer.parseInt(parts2[4]);
            int originalita = Integer.parseInt(parts2[5]);
            int edizione = Integer.parseInt(parts2[6]);
            int votoFinale = Integer.parseInt(parts2[7]);
            String recensione = parts2[8];



            //controllo se il libro è nella libreria dell'utente
            for (Library library : libraryDAO.getUserLibraries(idUtente)) {
                if (libraryDAO.bookExistsInLibrary(library.getIdLibreria(), idLibro)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return "RATING_FAILED:Il libro non è presente nella libreria dell'utente";
            }


            // Crea l'oggetto valutazione
            Rating rating = new Rating(idUtente, idLibro, stile, contenuto, gradevolezza,
                    originalita, edizione, votoFinale, recensione);

            // Salva la valutazione nel database
            boolean success = ratingDAO.salvaSuDatabase(rating);

            if (success) {
                return "RATING_SUCCESS:Valutazione inserita con successo";
            } else {
                return "RATING_FAILED:Errore durante il salvataggio della valutazione";
            }
        } catch (NumberFormatException e) {
            return "RATING_FAILED:Formato dei parametri non valido: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            return "RATING_FAILED:" + e.getMessage();
        } catch (Exception e) {
            return "RATING_FAILED:Errore imprevisto: " + e.getMessage();
        }
    }

    private String handleGetRatingFromBook(String request) {
        try {
            String[] parts = request.split(":");
            if (parts.length < 2) {
                return "RATING_RETRIEVAL_FAILED:Parametri insufficienti";
            }

            int idLibro = Integer.parseInt(parts[1]);
            RatingDAO ratingDAO = new RatingDAO();
            List<Rating> ratings = ratingDAO.getRatingsFromBook(idLibro);
            if (ratings == null) {
                return "RATING_RETRIEVAL_FAILED:Nessuna valutazione trovata per il libro con ID: " + idLibro;
            }
            StringBuilder response = new StringBuilder("INIZIO_LISTA_RATING\n");
            for (Rating rating : ratings) {
                response.append("RATING:")
                        .append(rating.getIdUtente()).append("|||")
                        .append(rating.getIdLibro()).append("|||")
                        .append(rating.getStile()).append("|||")
                        .append(rating.getContenuto()).append("|||")
                        .append(rating.getGradevolezza()).append("|||")
                        .append(rating.getOriginalita()).append("|||")
                        .append(rating.getEdizione()).append("|||")
                        .append(rating.getVotoFinale()).append("|||")
                        .append(rating.getRecensione()).append("\n");
            }
            response.append("END_RATINGS");
            return response.toString();

        } catch (NumberFormatException e) {
            return "RATING_RETRIEVAL_FAILED:Formato dei parametri non valido: " + e.getMessage();
        } catch (Exception e) {
            return "RATING_RETRIEVAL_FAILED:Errore imprevisto: " + e.getMessage();
        }


    }

    private String handleGetLibrary(String request) {
        // Parsa la richiesta per ottenere userId
        String[] parts = request.split(":", 2);
        if (parts.length < 2) {
            return "LIBRARY_RETRIEVAL_FAILED:Formato richiesta non valido";
        }
        String userId = parts[1];
        // Utilizza il LibraryDAO per ottenere la libreria
        List<Library> libraries = libraryDAO.getUserLibraries(userId);

        // Converti la lista di librerie in una stringa formattata
        StringBuilder response = new StringBuilder("INIZIO_LISTA_LIBRERIE\n");
        for (Library library : libraries) {
            response.append("LIBRARY:")
                    .append(library.getIdLibreria()).append("|||")
                    .append(library.getNome()).append("|||")
                    .append(library.getUser_id()).append("\n");
        }
        response.append("END_LIBRARIES");
        return response.toString();
    }

    private String handleGetLibraryBooks(String request) {
        // Parsa la richiesta per ottenere userId e nome libreria
        String[] parts = request.split(":", 3);
        if (parts.length < 3) {
            return "LIBRARY_BOOKS_RETRIEVAL_FAILED:Formato richiesta non valido";
        }

        String userId = parts[1];
        String libraryName = parts[2];

        // Utilizza il LibraryDAO per ottenere i libri della libreria
        List<Book> books = libraryDAO.getLibraryBooks(userId, libraryName);
        return getString(books);

    }

    private String handleRegistration(String request) {
        try {
            // Formato: REGISTER:nome:cognome:codiceFiscale:email:passwordEncrypted
            String[] parts = request.split(":", 6);
            if (parts.length < 6) {
                return "REGISTRAZIONE FALLITA:Parametri insufficienti";
            }

            String nome = parts[1];
            String cognome = parts[2];
            String codiceFiscale = parts[3];
            String email = parts[4];
            String password = parts[5];

            Utente utente = new Utente(nome, cognome, codiceFiscale, email, password);
            return utenteDAO.registraUtente(utente);
        } catch (Exception e) {
            return "REGISTRAZIONE FALLITA:" + e.getMessage();
        }
    }

    private String handleLogin(String request) {
        try {
            // Formato: LOGIN:userId:passwordEncrypted
            String[] parts = request.split(":", 3);
            if (parts.length < 3) {
                return "LOGIN FAILED:Parametri insufficienti";
            }

            String userId = parts[1];
            String password = parts[2];

            return utenteDAO.loginUtente(userId, password);
        } catch (Exception e) {
            return "LOGIN FAILED:" + e.getMessage();
        }
    }

    private String handleGetBook(String request) {
        try {
            // Formato: GET_BOOKS_:number
            String[] parts = request.split(":");
            if (parts.length < 2) {
                return "ERRORE:Parametri insufficienti";
            }

            int number = Integer.parseInt(parts[1]);
            List<Book> books = bookDAO.getBooks(number);
            return getString(books);
        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }

    private String handleSearch(String request) {
        try {
            // Formato: SEARCH:tipo:termine[:anno]
            String[] parts = request.split(":");
            if (parts.length < 4) {
                return "ERRORE:Parametri insufficienti";
            }

            String searchType = parts[1];
            String searchTerm = parts[2];
            Integer year = null;
            String userId = parts[3];
            String currentPage = parts[4];
            String libraryName = null;


            if (searchType.equals("AUTHOR_YEAR") && parts.length >= 4) {
                try {
                    year = Integer.parseInt(parts[3]);
                } catch (NumberFormatException e) {
                    return "ERRORE:Anno non valido";
                }
            }
            if(currentPage.contains("library-books-view")) {
                libraryName = parts[5];
            }

            List<Book> books = bookDAO.searchBooks(searchType, searchTerm, year, userId, currentPage,libraryName);

            return getString(books);
        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }

    private String handleCreateLibrary(String request) {
        // Parsa la richiesta per ottenere userId e nome libreria
        String[] parts = request.split(":", 3);
        if (parts.length < 3) {
            return "LIBRARY_CREATION_FAILED:Formato richiesta non valido";
        }

        String userId = parts[1];
        String libraryName = parts[2];

        // Utilizza il LibraryDAO per creare la libreria
        return libraryDAO.createLibrary(userId, libraryName);
    }

    private String handleAddBookToLibrary(String request) {
        // Parsa la richiesta: ADD_BOOK_TO_LIBRARY:userID:libraryname:idbook
        String[] parts = request.split(":", 4);
        if (parts.length < 4) {
            return "BOOK_ADD_FAILED:Formato richiesta non valido";
        }

        String userId = parts[1];
        String libraryName = parts[2];

        try {
            int bookId = Integer.parseInt(parts[3]);

            // Aggiungi il libro alla libreria usando l'ID
            return libraryDAO.addBookToLibrary(userId, libraryName, bookId);
        } catch (NumberFormatException e) {
            return "BOOK_ADD_FAILED:ID libro non valido";
        }
    }

    private void closeResources() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null) clientSocket.close();

            // Chiudi le connessioni ai DAO
            if (bookDAO != null) bookDAO.closeConnection();
            if (utenteDAO != null) utenteDAO.closeConnection();
            if (libraryDAO != null) libraryDAO.closeConnection();

        } catch (IOException e) {
            System.err.println("Errore nella chiusura delle risorse: " + e.getMessage());
        }
    }

    private String getString(List<Book> books) {
        StringBuilder response = new StringBuilder("INIZIO_LISTA_LIBRI\n");
        for (Book book : books) {
            response.append("BOOK:")
                    .append(book.getId()).append("|||")
                    .append(book.getTitle()).append("|||")
                    .append(book.getAuthor()).append("|||")
                    .append(book.getCategory()).append("|||")
                    .append(book.getPublisher()).append("|||")
                    .append(book.getPublicationYear()).append("|||")
                    .append(book.getCoverUrl()).append("\n");
        }
        response.append("END_BOOKS");
        return response.toString();
    }

}