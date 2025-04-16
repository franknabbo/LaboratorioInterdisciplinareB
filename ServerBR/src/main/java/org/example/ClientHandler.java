package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.example.db.Book;
import org.example.db.BookDAO;
import org.example.db.UtenteDAO;
import org.example.db.Utente;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private UtenteDAO utenteDAO;
    private BookDAO bookDAO;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.utenteDAO = new UtenteDAO();
        this.bookDAO = new BookDAO();
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    @Override
    public void run() {
        try {
            out.println("Benvenuto nel server BookRecommender!");
            String richiesta;

            while ((richiesta = in.readLine()) != null) {
                String[] partiRichiesta = richiesta.split(":");
                String comando = partiRichiesta[0];

                switch (comando) {
                    case "LOGIN":
                        if (partiRichiesta.length == 3) {
                            String userId = partiRichiesta[1];
                            String password = partiRichiesta[2];
                            String risultatoLogin = utenteDAO.loginUtente(userId, password);
                            out.println(risultatoLogin);
                        } else {
                            out.println("FORMATO DATI ERRATO - Formato corretto: LOGIN:userId:password");
                        }
                        break;

                    case "REGISTER":
                        if (partiRichiesta.length == 6) {
                            Utente nuovo = new Utente(
                                    partiRichiesta[1], // nome
                                    partiRichiesta[2], // cognome
                                    partiRichiesta[3], // cf
                                    partiRichiesta[4], // email
                                    partiRichiesta[5]  // password
                            );
                            String risultatoRegistrazione = utenteDAO.registraUtente(nuovo);
                            out.println(risultatoRegistrazione);
                        } else {
                            out.println("FORMATO DATI ERRATO - Formato corretto: REGISTER:nome:cognome:cf:email:password");
                        }
                        break;
                    case "GET_BOOK_COVERS":
                        if (partiRichiesta.length == 2) {
                            String limitParam = partiRichiesta[1];
                            processBookCoversRequest(limitParam);
                        } else {
                            out.println("FORMATO DATI ERRATO - Formato corretto: GET_BOOK_COVERS:numero");
                        }
                        break;
                    case "SEARCH":
                        if (partiRichiesta.length >= 2) {

                            // Gestione ricerca avanzata
                            if (partiRichiesta.length >= 3 && (partiRichiesta[1].equals("TITLE") ||
                                    partiRichiesta[1].equals("AUTHOR") ||
                                    partiRichiesta[1].equals("AUTHOR_YEAR"))) {
                                String searchType = partiRichiesta[1];

                                if (searchType.equals("AUTHOR_YEAR") && partiRichiesta.length == 4) {
                                    // Formato: SEARCH:AUTHOR_YEAR:autore:anno
                                    String author = partiRichiesta[2];
                                    try {
                                        int year = Integer.parseInt(partiRichiesta[3]);
                                        processAuthorYearSearch(author, year);
                                    } catch (NumberFormatException e) {
                                        out.println("ERRORE:L'anno deve essere un numero valido");
                                    }
                                } else {
                                    // Formato: SEARCH:TITLE:termine o SEARCH:AUTHOR:termine
                                    String searchTerm = partiRichiesta[2];
                                    if (searchType.equals("TITLE")) {
                                        processTitleSearch(searchTerm);
                                    } else if (searchType.equals("AUTHOR")) {
                                        processAuthorSearch(searchTerm);
                                    }
                                }
                            } else {
                                // Ricerca standard: SEARCH:termine
                                String searchTerm = partiRichiesta[1];
                                processSearchRequest(searchTerm);
                            }
                        } else {
                            out.println("FORMATO DATI ERRATO - Formato corretto: SEARCH:termine o SEARCH:TIPO:termine");
                        }
                        break;
                    case "QUIT":
                        out.println("DISCONNESSO");
                        return;

                    default:
                        out.println("COMANDO NON RICONOSCIUTO");
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Connessione terminata: " + e.getMessage());
        } finally {
            try {
                if (utenteDAO != null) {
                    utenteDAO.closeConnection();
                }
                if (bookDAO != null) {
                    bookDAO.closeConnection();
                }
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Errore durante la chiusura delle risorse: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void processTitleSearch(String title) {
        try {


            List<Book> books = bookDAO.searchBooksByTitle(title);

            // Invia i risultati al client
            sendBooksToClient(books);

        } catch (Exception e) {
            handleSearchError(e);
        }
    }

    private void processAuthorSearch(String author) {
        try {

            List<Book> books = bookDAO.searchBooksByAuthor(author);

            // Invia i risultati al client
            sendBooksToClient(books);

        } catch (Exception e) {
            handleSearchError(e);
        }
    }

    private void processAuthorYearSearch(String author, int year) {
        try {

            List<Book> books = bookDAO.searchBooksByAuthorAndYear(author, year);

            // Invia i risultati al client
            sendBooksToClient(books);

        } catch (Exception e) {
            handleSearchError(e);
        }
    }

    private void sendBooksToClient(List<Book> books) throws InterruptedException {
        out.println("INIZIO_LISTA_LIBRI");

        if (books.isEmpty()) {
            out.println("INFO:Nessun libro trovato");
        } else {
            int count = 1;
            for (Book book : books) {
                try {
                    // Utilizziamo la copertina dal database o ne richiediamo una nuova
                    String coverUrl = book.getCoverUrl();
                    if (coverUrl == null || coverUrl.isEmpty()) {
                        coverUrl = bookDAO.getBookCover(book.getTitle(), book.getAuthor());
                    }

                    String bookData = String.format("BOOK:%s:%s:%s:%s",
                            book.getTitle(),
                            book.getAuthor(),
                            book.getDescription(),
                            coverUrl != null ? coverUrl : "null");
                    out.println(bookData);

                    count++;
                    Thread.sleep(50);
                } catch (Exception e) {
                    System.err.println("Errore durante l'elaborazione del libro " +
                            book.getTitle() + ": " + e.getMessage());
                }
            }
        }

        out.println("END_BOOKS");
    }

    private void handleSearchError(Exception e) {
        System.err.println("Errore nella ricerca: " + e.getMessage());
        e.printStackTrace();
        out.println("ERRORE:Si è verificato un errore durante la ricerca");
        out.println("END_BOOKS");
    }

    private void processSearchRequest(String searchTerm) {
        try {

            // Cerca per titolo e per autore
            List<Book> booksByTitle = bookDAO.searchBooksByTitle(searchTerm);

            List<Book> booksByAuthor = bookDAO.searchBooksByAuthor(searchTerm);

            // Unisci i risultati evitando duplicati
            Set<Integer> bookIds = new HashSet<>();
            List<Book> results = new ArrayList<>();

            // Aggiungi libri dal titolo
            for (Book book : booksByTitle) {
                if (!bookIds.contains(book.getId())) {
                    results.add(book);
                    bookIds.add(book.getId());
                }
            }

            // Aggiungi libri dall'autore
            for (Book book : booksByAuthor) {
                if (!bookIds.contains(book.getId())) {
                    results.add(book);
                    bookIds.add(book.getId());
                }
            }


            // Invia i risultati al client
            out.println("INIZIO_LISTA_LIBRI");

            if (results.isEmpty()) {
                System.out.println("Nessun libro trovato per: \"" + searchTerm + "\"");
                out.println("INFO:Nessun libro trovato per: " + searchTerm);
            } else {
                int count = 1;

                for (Book book : results) {
                    try {
                        // Stampa dettagli libro

                        // Utilizziamo la copertina dal database o ne richiediamo una nuova
                        String coverUrl = book.getCoverUrl();
                        if (coverUrl == null || coverUrl.isEmpty()) {
                            coverUrl = bookDAO.getBookCover(book.getTitle(), book.getAuthor());
                        }

                        String bookData = String.format("BOOK:%s:%s:%s:%s",
                                book.getTitle(),
                                book.getAuthor(),
                                book.getDescription(),
                                coverUrl != null ? coverUrl : "null");
                        out.println(bookData);

                        count++;

                        // Breve pausa per evitare sovraccarichi nella comunicazione
                        Thread.sleep(50);
                    } catch (Exception e) {
                        System.err.println("Errore durante l'elaborazione del libro " +
                                book.getTitle() + ": " + e.getMessage());
                        // Continuiamo con il prossimo libro anche se c'è un errore con questo
                    }
                }
            }

            // Segnala la fine dell'elenco
            out.println("END_BOOKS");
            System.out.println("===== FINE RICERCA =====\n");

        } catch (Exception e) {
            System.err.println("Errore generale nella gestione della ricerca: " + e.getMessage());
            e.printStackTrace();
            out.println("ERRORE:Si è verificato un errore durante la ricerca");
            out.println("END_BOOKS"); // Assicura che il client sappia che la trasmissione è terminata
        }
    }

    // Invia ogni libro con la sua copertina

    private void processBookCoversRequest(String limitParam) {
        try {
            // Validazione dell'input
            int limit;
            try {
                limit = Integer.parseInt(limitParam);
                if (limit <= 0) {
                    out.println("ERRORE:Il limite deve essere un numero positivo");
                    return;
                }
            } catch (NumberFormatException e) {
                out.println("ERRORE:Il parametro limit deve essere un numero valido");
                System.err.println("Errore nel parsing del limite: " + e.getMessage());
                return;
            }

            // Inizio della trasmissione
            out.println("INIZIO_LISTA_LIBRI");

            try {
                // Ottieni libri dal database in ordine
                List<Book> books = bookDAO.getBooks(limit);

                if (books.isEmpty()) {
                    out.println("INFO:Nessun libro trovato");
                } else {
                    // TODO: Questo for rallenta tutto perché prima hai gia caricato su una lista tutto
                    // TODO Dovresti usare un generatore, se esiste in java, che nel mente che li prende (con getBooks), li manda al client
                    // Invia i libri al client
                    for (Book book : books) {
                        try {
                            // Utilizziamo la copertina dal database o ne richiediamo una nuova
                            String coverUrl = book.getCoverUrl();
                            if (coverUrl == null || coverUrl.isEmpty()) {
                                coverUrl = bookDAO.getBookCover(book.getTitle(), book.getAuthor());
                            }

                            String bookData = String.format("BOOK:%s:%s:%s:%s",
                                    book.getTitle(),
                                    book.getAuthor(),
                                    book.getDescription(),
                                    coverUrl != null ? coverUrl : "null");
                            out.println(bookData);

                            // Breve pausa per evitare sovraccarichi nella comunicazione
                            Thread.sleep(10);

                        } catch (Exception e) {
                            System.err.println("Errore durante l'elaborazione del libro " +
                                    book.getTitle() + ": " + e.getMessage());
                            // Continuiamo con il prossimo libro anche se c'è un errore con questo
                        }
                    }
                }
            } catch (Exception e) {
                out.println("ERRORE:Problema nel recupero dei libri: " + e.getMessage());
                System.err.println("Errore nel recupero dei libri: " + e.getMessage());
                e.printStackTrace();
            }

            // Segnala la fine dell'elenco in ogni caso
            out.println("END_BOOKS");

        } catch (Exception e) {
            System.err.println("Errore generale nella gestione della richiesta GET_BOOK_COVERS: " + e.getMessage());
            out.println("ERRORE:Si è verificato un errore imprevisto");
            out.println("END_BOOKS"); // Assicura che il client sappia che la trasmissione è terminata
        }
    }
}