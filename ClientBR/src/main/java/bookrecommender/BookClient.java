// Francesco Del Rosso (Matricola: 758295) Como
// Davide Cartolano (Matricola: 757603) Como
// Tommaso Ferloni (Matricola: 757581) Como
// Andrea Riva (Matricola: 757580) Como
package bookrecommender;

import bookrecommender.controller.SceneController;
import bookrecommender.controller.UserManager;
import bookrecommender.Book;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Client per la gestione dei libri, con metodi per richieste e parsing delle risposte dal server.
 */
public class BookClient {

    /**
     * Richiede un numero specifico di libri dal server.
     * @param number numero di libri da richiedere
     * @return lista di libri ottenuti
     * @throws IOException in caso di errori di I/O
     */
    public List<Book> getBooks(int number) throws IOException {
        List<Book> books = new ArrayList<>();

        SocketConnection.sendMessage("GET_BOOKS:" + number);
        BufferedReader in = SocketConnection.getIn();
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
                    String[] parts = line.split("BOOK:|\\|\\|\\|");
                    if (parts.length >= 8) {
                        Book book = getBook(parts);
                        books.add(book);
                    } else {
                        System.err.println("Formato libro non valido: " + line);
                    }
                } catch (Exception e) {
                    System.err.println("Errore nel parsing dei dati del libro: " + e.getMessage());
                }
            }
        }
        return books;
    }

    /**
     * Crea un oggetto Book da un array di stringhe parsate dal server.
     * @param parts array con i dati del libro
     * @return oggetto Book costruito
     */
    public static Book getBook(String[] parts) {
        int id = Integer.parseInt(parts[1]);
        String title = parts[2];
        String author = parts[3];
        String category = parts[4];
        String publisher = parts[5];
        String publicationYear = parts[6];

        String coverUrl = "null";
        if (parts.length > 7) {
            coverUrl = parts[7];
        }

        return new Book(id, title, author, category, publisher, publicationYear, coverUrl);
    }

    /**
     * Richiede i libri appartenenti a una libreria specifica di un utente.
     * @param userId id utente
     * @param libraryName nome della libreria
     * @return lista di libri nella libreria
     * @throws IOException in caso di errori di I/O
     */
    public List<Book> getLibraryBooks(String userId, String libraryName) throws IOException {
        List<Book> books = new ArrayList<>();
        String request = "GET_LIBRARY_BOOKS:" + userId + ":" + libraryName;

        SocketConnection.sendMessage(request);
        BufferedReader in = SocketConnection.getIn();
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
                    String[] parts = line.split("BOOK:|\\|\\|\\|");
                    if (parts.length >= 8) {
                        int id = Integer.parseInt(parts[1]);
                        String title = parts[2];
                        String author = parts[3];
                        String category = parts[4];
                        String publisher = parts[5];
                        String publicationYear = parts[6];
                        String coverUrl = parts[7];

                        Book book = new Book(id, title, author, category, publisher, publicationYear, coverUrl);
                        books.add(book);
                    }
                } catch (Exception e) {
                    System.err.println("Errore nel parsing: " + e.getMessage());
                }
            }
        }
        return books;
    }

    /**
     * Esegue una ricerca di libri sul server.
     * @param searchType tipo di ricerca (es. AUTHOR, TITLE, AUTHOR_YEAR)
     * @param searchTerm termine da cercare
     * @param year anno (opzionale, usato solo se searchType è AUTHOR_YEAR)
     * @return lista di libri trovati
     * @throws IOException in caso di errori di I/O
     */
    public List<Book> performSearch(String searchType, String searchTerm, String year) throws IOException {
        String request = "SEARCH:" + searchType + ":" + searchTerm;
        if (year != null && searchType.equals("AUTHOR_YEAR")) {
            request += ":" + year;
        }
        request += ":" + UserManager.getUserId();
        request += ":" + SceneController.currentPage;
        if (SceneController.currentPage.contains("library-books-view")) {
            request += ":" + SceneController.currentLibrary;
        }
        SocketConnection.sendMessage(request);
        return parseSearchResults();
    }

    /**
     * Parsea la risposta del server contenente risultati di ricerca.
     * @return lista di libri trovati
     * @throws IOException in caso di errori di I/O
     */
    private List<Book> parseSearchResults() throws IOException {
        List<Book> results = new ArrayList<>();
        String line;
        boolean reading = false;

        BufferedReader in = SocketConnection.getIn();

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
                    String[] parts = line.split("BOOK:|\\|\\|\\|");
                    if (parts.length >= 8) {
                        int id = Integer.parseInt(parts[1]);
                        String title = parts[2];
                        String author = parts[3];
                        String category = parts[4];
                        String publisher = parts[5];
                        String publicationYear = parts[6];
                        String coverUrl = parts[7];

                        Book book = new Book(id, title, author, category, publisher, publicationYear, coverUrl);
                        results.add(book);
                    }
                } catch (Exception e) {
                    System.err.println("Errore nel parsing: " + e.getMessage());
                }
            }
        }
        return results;
    }
}
