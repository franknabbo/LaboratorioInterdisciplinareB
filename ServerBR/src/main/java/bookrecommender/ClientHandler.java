// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como

package bookrecommender;
import bookrecommender.db.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestisce la comunicazione con un singolo client tramite socket.
 * Implementa Runnable per poter essere eseguito in un thread separato.
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final BookDAO bookDAO;
    private final UtenteDAO utenteDAO;
    private LibraryDAO libraryDAO;
    private SuggestedBookDAO suggestedBookDAO;
    private final RatingDAO ratingDAO;

    /**
     * Costruttore che inizializza il socket client e i DAO.
     *
     * @param socket Socket associato al client.
     */
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.bookDAO = new BookDAO();
        this.utenteDAO = new UtenteDAO();
        this.libraryDAO = new LibraryDAO();
        this.suggestedBookDAO = new SuggestedBookDAO();
        this.ratingDAO = new RatingDAO();
    }

    /**
     * Metodo run eseguito dal thread per gestire le richieste del client.
     * Legge le richieste dalla socket e invia le risposte.
     */
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

    /**
     * Processa la richiesta ricevuta dal client e ritorna la risposta.
     *
     * @param request Richiesta come stringa.
     * @return Risposta da inviare al client.
     */
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
        } else if (request.startsWith("ADD_SUGGESTED_BOOK:")) {
            return handleAddSuggestedBook(request);
        } else if (request.startsWith("GET_SUGGESTED_BOOKS:")) {
            return handleGetSuggestedBooks(request);
        } else {
            return "ERRORE:Comando non riconosciuto";
        }
    }

    /**
     * Gestisce la richiesta di aggiungere libri suggeriti.
     * Formato richiesta: ADD_SUGGESTED_BOOK:userId:idLibroReferenced:idLibroSuggested:idLibroSuggested2:...
     *
     * @param request Stringa richiesta ricevuta.
     * @return Risposta relativa all'operazione.
     */
    private String handleAddSuggestedBook(String request) {
        // Rimuove il prefisso dalla richiesta
        request = request.replace("ADD_SUGGESTED_BOOK:", "");

        String[] parts = request.split(":");

        String userId = parts[0];
        int idLibroReferenced = Integer.parseInt(parts[1]);

        List<Integer> idLibri = new ArrayList<>();
        // Prende tutti i libri suggeriti da indicizzazione 2 in poi
        for (int i = 2; i < parts.length; i++) {
            idLibri.add(Integer.parseInt(parts[i]));
        }

        // Controlla se il libro referenziato è nella libreria dell'utente
        boolean flag = false;
        for (Library library : libraryDAO.getUserLibraries(userId)) {
            if (libraryDAO.bookExistsInLibrary(library.getIdLibreria(), idLibroReferenced)) {
                flag = true;
                break;
            }
        }

        // Aggiungi il libro suggerito se autorizzato
        if (flag && suggestedBookDAO.addSuggestedBook(userId, idLibroReferenced, idLibri)) {
            return "SUGGESTION_SUCCESS:Libro suggerito aggiunto con successo";
        } else {
            return "ADD_SUGGESTED_BOOK_FAILED:Errore durante l'aggiunta del libro suggerito";
        }
    }

    /**
     * Gestisce la richiesta di ottenere libri suggeriti per un libro specifico.
     * Formato richiesta: GET_SUGGESTED_BOOKS:idLibro
     *
     * @param request Stringa richiesta ricevuta.
     * @return Lista dei libri suggeriti come stringa oppure messaggio di errore.
     */
    private String handleGetSuggestedBooks(String request) {
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
            books.add(bookDAO.getBookDetails(suggestedBook));
        }
        System.out.println(getString(books));
        return getString(books);
    }

    /**
     * Gestisce l'aggiunta di una valutazione per un libro.
     * Formato richiesta: ADD_RATING:idUtente|||idLibro|||stile|||contenuto|||gradevolezza|||originalita|||edizione|||votofinale|||recensione
     *
     * @param request Stringa richiesta ricevuta.
     * @return Risposta relativa all'operazione di inserimento valutazione.
     */
    private String handleAddRating(String request) {
        try {
            boolean flag = false;

            String[] parts = request.split(":");
            if (parts.length < 2) {
                return "RATING_FAILED:Parametri insufficienti";
            }
            String[] parts2 = parts[1].split("\\|\\|\\|");

            if (parts2.length < 9) {
                return "RATING_FAILED:Parametri insufficienti";
            }

            String idUtente = parts2[0];
            int idLibro = Integer.parseInt(parts2[1]);
            int stile = Integer.parseInt(parts2[2]);
            int contenuto = Integer.parseInt(parts2[3]);
            int gradevolezza = Integer.parseInt(parts2[4]);
            int originalita = Integer.parseInt(parts2[5]);
            int edizione = Integer.parseInt(parts2[6]);
            int votoFinale = Integer.parseInt(parts2[7]);
            String recensione = parts2[8];

            // Controlla se il libro è nella libreria dell'utente
            for (Library library : libraryDAO.getUserLibraries(idUtente)) {
                if (libraryDAO.bookExistsInLibrary(library.getIdLibreria(), idLibro)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return "RATING_FAILED:Il libro non è presente nella libreria dell'utente";
            }

            //controllo se esiste gia una valutazione per questo libro di questo utente
            if (ratingDAO.ratingExists(idUtente, idLibro)) {
                return "RATING_FAILED:Esiste già una valutazione per questo libro da parte di questo utente";
            }

            // Crea oggetto Rating e salva su database
            Rating rating = new Rating(idUtente, idLibro, stile, contenuto, gradevolezza,
                    originalita, edizione, votoFinale, recensione);

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


    /**
     * Recupera le valutazioni associate a un libro dato il suo ID.
     * La richiesta ha formato: "RATING:idLibro".
     *
     * @param request la stringa di richiesta contenente l'ID del libro
     * @return una stringa formattata con la lista delle valutazioni o un messaggio di errore
     */
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

    /**
     * Recupera le librerie associate a un utente.
     * La richiesta ha formato: "GET_LIBRARY:userId".
     *
     * @param request la stringa di richiesta contenente l'ID utente
     * @return una stringa formattata con la lista delle librerie o un messaggio di errore
     */
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

    /**
     * Recupera i libri presenti in una libreria specifica di un utente.
     * La richiesta ha formato: "GET_LIBRARY_BOOKS:userId:libraryName".
     *
     * @param request la stringa di richiesta contenente userId e nome libreria
     * @return una stringa formattata con la lista dei libri o un messaggio di errore
     */
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

    /**
     * Gestisce la registrazione di un nuovo utente.
     * La richiesta ha formato: "REGISTER:nome:cognome:codiceFiscale:email:passwordEncrypted".
     *
     * @param request la stringa di richiesta con i dati dell'utente
     * @return esito della registrazione o messaggio di errore
     */
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

    /**
     * Gestisce il login di un utente.
     * La richiesta ha formato: "LOGIN:userId:passwordEncrypted".
     *
     * @param request la stringa di richiesta contenente userId e password crittografata
     * @return esito del login o messaggio di errore
     */
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

    /**
     * Recupera un certo numero di libri.
     * La richiesta ha formato: "GET_BOOKS:number".
     *
     * @param request la stringa di richiesta con il numero di libri da ottenere
     * @return una stringa formattata con la lista dei libri o un messaggio di errore
     */
    private String handleGetBook(String request) {
        try {
            // Formato: GET_BOOKS:number
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

    /**
     * Gestisce la ricerca di libri in base a vari criteri.
     * La richiesta ha formato: "SEARCH:tipo:termine[:anno]:userId:currentPage[:libraryName]".
     *
     * @param request la stringa di richiesta contenente i parametri di ricerca
     * @return una stringa formattata con i libri trovati o un messaggio di errore
     */
    private String handleSearch(String request) {
        try {
            String[] parts = request.split(":");

            if (parts.length < 5) {
                return "ERRORE:Parametri insufficienti";
            }

            String searchType = parts[1];
            String searchTerm = parts[2];
            Integer year = null;
            String userId;
            String currentPage;
            String libraryName = null;

            int index = 3;

            // Caso con anno
            if (searchType.equals("AUTHOR_YEAR")) {
                try {
                    year = Integer.parseInt(parts[index++]);
                } catch (NumberFormatException e) {
                    return "ERRORE:Anno non valido";
                }
            }

            // Proseguo a leggere userId e currentPage
            if (parts.length <= index + 1) {
                return "ERRORE:Parametri insufficienti dopo l'anno";
            }

            userId = parts[index++];
            currentPage = parts[index++];

            if (currentPage.contains("library-books-view")) {
                if (parts.length <= index) {
                    return "ERRORE:Nome della libreria mancante";
                }
                libraryName = parts[index];
            }

            List<Book> books = bookDAO.searchBooks(searchType, searchTerm, year, currentPage, libraryName);

            return getString(books);
        } catch (Exception e) {
            return "ERRORE:" + e.getMessage();
        }
    }


    /**
     * Crea una nuova libreria per un utente.
     * La richiesta ha formato: "CREATE_LIBRARY:userId:libraryName".
     *
     * @param request la stringa di richiesta contenente userId e nome libreria
     * @return esito della creazione o messaggio di errore
     */
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

    /**
     * Aggiunge un libro a una libreria specifica di un utente.
     * La richiesta ha formato: "ADD_BOOK_TO_LIBRARY:userId:libraryName:bookId".
     *
     * @param request la stringa di richiesta contenente userId, nome libreria e ID libro
     * @return esito dell'operazione o messaggio di errore
     */
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

    /**
     * Chiude tutte le risorse aperte, compresi stream, socket e connessioni ai DAO.
     */
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

    /**
     * Converte una lista di oggetti Book in una stringa formattata.
     *
     * @param books lista di libri da convertire in stringa
     * @return stringa formattata contenente i dati dei libri
     */
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